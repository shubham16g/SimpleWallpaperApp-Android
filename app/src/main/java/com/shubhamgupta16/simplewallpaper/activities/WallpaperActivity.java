package com.shubhamgupta16.simplewallpaper.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.shubhamgupta16.simplewallpaper.Application;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.Utils;
import com.shubhamgupta16.simplewallpaper.utils.WallpaperSetter;

public class WallpaperActivity extends AppCompatActivity {

    private static final String TAG = "WallpaperActivity";

    private Bitmap imageBitmap;
    private SQLHelper sqlHelper;
    private WallsPOJO pojo;

    //    Views
    private PhotoView photoView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private View topShadow, bottomShadow, saveButton, applyButton, favoriteButton;
    private LinearLayout bottomNavLayout;

    //    ads
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private AdView bannerAdView;

    //    conditions for not showing ads multiple times
    boolean isInterstitialApplyShown = false;
    boolean isInterstitialSaveShown = false;
    boolean isRewardedApplyShown = false;
    boolean isRewardedSaveShown = false;

    //    dynamic message showing on task complete and ad shown
    private boolean isProcessCompleted = false;
    private String message;

    private void processStart(String message) {
        progressBar.setVisibility(View.VISIBLE);
        this.message = message;
        isProcessCompleted = true;
    }

    private void processStopIfDone() {
        if (isProcessCompleted) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }, 800);
        }
        isProcessCompleted = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

//        get the wallpaper pojo. if not exist, finish page.
        if (!getIntent().hasExtra("pojo")) {
            Toast.makeText(this, "Image Not Valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pojo = (WallsPOJO) getIntent().getSerializableExtra("pojo");

//        apply full screen
        View mDecorView = getWindow().getDecorView();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.parseColor("#01FFFFFF"));

//        bind xml views
        toolbar = findViewById(R.id.full_view_toolbar);
        topShadow = findViewById(R.id.top_shadow);
        bottomShadow = findViewById(R.id.bottom_shadow);
        photoView = findViewById(R.id.photo_view);
        progressBar = findViewById(R.id.full_progressbar);
        bottomNavLayout = findViewById(R.id.bottomButtonNav);
        bannerAdView = findViewById(R.id.adView);
        saveButton = bottomNavLayout.getChildAt(0);
        applyButton = bottomNavLayout.getChildAt(1);
        favoriteButton = bottomNavLayout.getChildAt(2);

//        apply margin for status-bar and navigation-bar
        ViewCompat.setOnApplyWindowInsetsListener(mDecorView, (v, insets) -> {
            final int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top; // in px
            final int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom; // in px

            RelativeLayout.LayoutParams toolbarLayoutParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            toolbarLayoutParams.setMargins(0, statusBarHeight, 0, 0);
            toolbar.setLayoutParams(toolbarLayoutParams);

            RelativeLayout.LayoutParams navLayoutLayoutParams = (RelativeLayout.LayoutParams) bannerAdView.getLayoutParams();
            navLayoutLayoutParams.setMargins(0, 0, 0, navigationBarHeight);
            bannerAdView.setLayoutParams(navLayoutLayoutParams);
            return WindowInsetsCompat.CONSUMED;
        });

//        init helpers and ads
        sqlHelper = new SQLHelper(this);
        initAd();
        loadInterstitial();

//        init views
        setupBottomNav();
        toolbar.setNavigationOnClickListener(v -> finish());
        if (pojo.isPremium()) {
            findViewById(R.id.premiumImage).setVisibility(View.VISIBLE);
        }
        photoView.setOnPhotoTapListener((view, x, y) -> toggleTouch());
        photoView.setTag(false);

//        load blur image into photoView
        Glide.with(this).asBitmap().load(pojo.getPreviewUrl()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (photoView.getTag().equals(false)) {
                    photoView.setImageBitmap(fastBlur(WallpaperActivity.this, resource));
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

//        load high res image into photoView
        Glide.with(this).asBitmap().load(pojo.getUrl()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(WallpaperActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageBitmap = resource;
                photoView.setImageBitmap(imageBitmap);
                photoView.setTag(true);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    private void initAd() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
    }

    private void showInterstitial(boolean isForSaveImage) {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                    processStopIfDone();
                    loadInterstitial();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                    processStopIfDone();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    if (isForSaveImage) {
                        isInterstitialSaveShown = true;
                    } else {
                        isInterstitialApplyShown = true;
                    }
                }
            });
            mInterstitialAd.show(this);
        } else {
            processStopIfDone();
        }
    }

    private void loadInterstitial() {
//        never load interstitial for premium wallpaper
        if (pojo.isPremium()) return;

        InterstitialAd.load(this, getString(R.string.set_wallpaper_interstitial_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }

    private void setupBottomNav() {
        final ImageView heartImage = favoriteButton.findViewById(R.id.heartImage);

        if (sqlHelper.isFavorite(pojo.getId()))
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
        else
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
//        save button click
        saveButton.setOnClickListener(view -> {
            if (isStoragePermissionNotGranted()) {
                ActivityCompat.requestPermissions(WallpaperActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
            if (pojo.isPremium() && !isRewardedSaveShown) {
                showWatchAdDialog(true);
                return;
            }
            saveImage();
        });

//        apply button click
        applyButton.setOnClickListener(view -> {
            if (pojo.isPremium() && !isRewardedApplyShown) {
                showWatchAdDialog(false);
                return;
            }
            askOrApplyWallpaper();
        });
//        favorite button click
        favoriteButton.setOnClickListener(view -> {
            if (sqlHelper.isFavorite(pojo.getId())) {
                sqlHelper.toggleFavorite(pojo.getId(), false);
                heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            } else {
                sqlHelper.toggleFavorite(pojo.getId(), true);
                heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });
    }

    public boolean isStoragePermissionNotGranted() {
//        if api is below 29 and above/equal 23, WRITE_EXTERNAL_STORAGE required
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        }
    }

    //    apply blur on bitmap
    private static Bitmap fastBlur(Context context, Bitmap source) {
        Bitmap bitmap = source.copy(source.getConfig(), true);
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(18);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }

    private void saveImage() {
        if (imageBitmap == null || pojo == null) return;
        Log.d("TAG", "saveImage: called");
        if (isStoragePermissionNotGranted())
            return;

//        saving
        processStart("Image Saved Successfully!");
        final boolean isSaved = Utils.save(this, imageBitmap, getString(R.string.app_name),
                pojo.getName().replaceAll("\\s", "_"));
//        delay of 500ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isSaved) {
                if (isInterstitialSaveShown)
                    processStopIfDone();
                else
                    showInterstitial(true);
            } else {
                message = "Error while saving image.";
                processStopIfDone();
            }
        }, 500);

    }

    private void askOrApplyWallpaper() {
        View v = getLayoutInflater().inflate(R.layout.layout_set_on, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AlertDialog dialog = new AlertDialog.Builder(WallpaperActivity.this)
                    .setView(v).create();
            v.findViewById(R.id.on_home_screen_btn).setOnClickListener(view -> {
                dialog.dismiss();
                applyWallpaper(1);
            });
            v.findViewById(R.id.on_lock_screen_btn).setOnClickListener(view -> {
                dialog.dismiss();
                applyWallpaper(2);
            });
            v.findViewById(R.id.on_both_screen_btn).setOnClickListener(view -> {
                dialog.dismiss();
                applyWallpaper(3);
            });
            dialog.show();
        } else {
            applyWallpaper(0);
        }
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish: called -> " + (mInterstitialAd != null));
        if (!(getApplication() instanceof Application)){
            super.finish();
        }
        final Application application = (Application) getApplication();
        if (mInterstitialAd != null && !isInterstitialApplyShown && !isInterstitialSaveShown && application.canShowInterstitial()) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                    finish();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                    finish();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    application.interstitialShown();
                }
            });
            mInterstitialAd.show(this);
        } else {
            super.finish();
        }
//        super.finish();
    }

    private void toggleTouch() {
        if (toolbar.getAlpha() == 0) {
            toolbar.animate().alpha(1).setDuration(200);
            topShadow.animate().alpha(1).setDuration(200);
            bottomShadow.animate().alpha(1).setDuration(200);
            bottomNavLayout.animate().alpha(1).setDuration(200);
            saveButton.setClickable(true);
            applyButton.setClickable(true);
            favoriteButton.setClickable(true);
            toolbar.setNavigationOnClickListener(v->finish());
        } else {
            toolbar.animate().alpha(0).setDuration(200);
            topShadow.animate().alpha(0).setDuration(200);
            bottomShadow.animate().alpha(0).setDuration(200);
            bottomNavLayout.animate().alpha(0).setDuration(200);
            saveButton.setClickable(false);
            applyButton.setClickable(false);
            favoriteButton.setClickable(false);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    private void applyWallpaper(int where) {
        processStart(getString(R.string.success_applied));
        WallpaperSetter.apply(this, imageBitmap, where, b -> {
            if (b) {
                if (isInterstitialApplyShown)
                    processStopIfDone();
                else
                    showInterstitial(false);
            } else {
                message = "Failed to apply wallpaper";
                processStopIfDone();
            }
        });
    }

    private void showWatchAdDialog(boolean isForSaveImage) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.download_premium_title)
                .setPositiveButton(R.string.yes_watch, (dialog1, which) -> loadRewardedAd(isForSaveImage))
                .setNegativeButton(R.string.no_thanks, null)
                .create().show();
    }

    private void loadRewardedAd(boolean isForSaveImage) {
        progressBar.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.rewarded_ad_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(WallpaperActivity.this, "Failed to load Ad.", Toast.LENGTH_SHORT).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        progressBar.setVisibility(View.GONE);
                        mRewardedAd = rewardedAd;
                        showRewardedAd(isForSaveImage);
                    }
                });
    }

    private void showRewardedAd(boolean isForSaveImage) {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, rewardItem -> {
                if (isForSaveImage) {
                    isRewardedSaveShown = true;
                    saveImage();
                } else {
                    isRewardedApplyShown = true;
                    askOrApplyWallpaper();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    @Override
    protected void onPause() {
        bannerAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bannerAdView.resume();
    }

    @Override
    protected void onDestroy() {
        bannerAdView.destroy();
        Intent i = new Intent();
        i.putExtra("id", pojo.getId());
        i.putExtra("fav", sqlHelper.isFavorite(pojo.getId()));
        setResult(RESULT_OK, i);
        super.onDestroy();
    }
}