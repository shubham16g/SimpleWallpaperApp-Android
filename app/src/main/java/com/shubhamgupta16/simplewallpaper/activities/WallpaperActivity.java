package com.shubhamgupta16.simplewallpaper.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.Utils;
import com.shubhamgupta16.simplewallpaper.utils.WallpaperSetter;

public class WallpaperActivity extends AppCompatActivity {

    private static final String TAG = "WallpaperActivity";

    private Bitmap imageBitmap;
    private PhotoView photoView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private View mDecorView, topShadow, bottomShadow;
    private LinearLayout bottomNavLayout;
    private SQLHelper sqlHelper;
    private WallsPOJO pojo;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        if (!getIntent().hasExtra("pojo")) {
            Toast.makeText(this, "Image Not Valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDecorView = getWindow().getDecorView();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.parseColor("#01FFFFFF"));

        showSysUI();

        toolbar = findViewById(R.id.full_view_toolbar);
        topShadow = findViewById(R.id.top_shadow);
        bottomShadow = findViewById(R.id.bottom_shadow);
        photoView = findViewById(R.id.photo_view);
        progressBar = findViewById(R.id.full_progressbar);
        bottomNavLayout = findViewById(R.id.bottomButtonNav);
        sqlHelper = new SQLHelper(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, 0);
        toolbar.setLayoutParams(params);

        pojo = (WallsPOJO) getIntent().getSerializableExtra("pojo");
        setupBottomNav();

        toolbar.setNavigationOnClickListener(v -> finish());
        initInterstitial();

        if (pojo.isPremium()) {
            findViewById(R.id.premiumImage).setVisibility(View.VISIBLE);
        }

        photoView.setOnPhotoTapListener((view, x, y) -> toggleTouch());

        photoView.setTag(false);
        Glide.with(this).asBitmap().load(pojo.getPreviewUrl()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                photoView.setCropToPadding(true);
                if (photoView.getTag().equals(false)) {
                    photoView.setImageBitmap(fastBlur(WallpaperActivity.this, resource));
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

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

    private void showInterstitial() {
        if (mInterstitialAd != null)
            mInterstitialAd.show(this);
    }

    private void initInterstitial() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        InterstitialAd.load(this, getString(R.string.set_wallpaper_interstitial_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }

    private void setupBottomNav() {
        View saveButton = bottomNavLayout.getChildAt(0);
        View applyButton = bottomNavLayout.getChildAt(1);
        View favoriteButton = bottomNavLayout.getChildAt(2);
        final ImageView heartImage = favoriteButton.findViewById(R.id.heartImage);

        if (sqlHelper.isFavorite(pojo.getUrl()))
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
        else
            heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
//        save button click
        saveButton.setOnClickListener(view -> {
            if (isStoragePermissionNotGranted()){
                ActivityCompat.requestPermissions(WallpaperActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
            if (pojo.isPremium()) {
                showWatchAdDialog(true);
                return;
            }
            saveImage();
        });

//        apply button click
        applyButton.setOnClickListener(view -> {
            if (pojo.isPremium()) {
                showWatchAdDialog(false);
                return;
            }
            askOrApplyWallpaper();
        });
        favoriteButton.setOnClickListener(view -> {
            if (sqlHelper.isFavorite(pojo.getUrl())) {
                sqlHelper.toggleFavorite(pojo.getUrl(), false);
                heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            } else {
                sqlHelper.toggleFavorite(pojo.getUrl(), true);
                heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });
    }

    public boolean isStoragePermissionNotGranted() {
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
        final boolean isSaved = Utils.save(this, imageBitmap, getString(R.string.app_name),
                pojo.getName().replaceAll("\\s", "_"));
        if (isSaved) {
            Toast.makeText(this, "Image Saved Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error while saving image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void askOrApplyWallpaper() {
        View v =  getLayoutInflater().inflate(R.layout.layout_set_on, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AlertDialog dialog = new AlertDialog.Builder(WallpaperActivity.this)
                    .setView(v).create();
            v.findViewById(R.id.on_home_screen_btn).setOnClickListener(view -> applyWallpaper(1));
            v.findViewById(R.id.on_lock_screen_btn).setOnClickListener(view -> applyWallpaper(2));
            v.findViewById(R.id.on_both_screen_btn).setOnClickListener(view -> applyWallpaper(3));
            dialog.show();
        } else {
            applyWallpaper(0);
        }
    }

    private void showSysUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void hideSysUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void toggleTouch() {
        if (toolbar.getAlpha() == 0) {
            toolbar.animate().alpha(1).setDuration(200);
            topShadow.animate().alpha(1).setDuration(200);
            bottomShadow.animate().alpha(1).setDuration(200);
            bottomNavLayout.animate().alpha(1).setDuration(200);
            showSysUI();
        } else {
            toolbar.animate().alpha(0).setDuration(200);
            topShadow.animate().alpha(0).setDuration(200);
            bottomShadow.animate().alpha(0).setDuration(200);
            bottomNavLayout.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {
                    if (bottomNavLayout.getVisibility() == View.VISIBLE) {
                        bottomNavLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    if (bottomNavLayout.getVisibility() == View.GONE) {
                        bottomNavLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
            hideSysUI();
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void applyWallpaper(int where) {
        progressBar.setVisibility(View.VISIBLE);
        WallpaperSetter.apply(this, imageBitmap, where, b -> {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(WallpaperActivity.this, WallpaperActivity.class);
            intent.putExtra("pojo", pojo);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Toast.makeText(WallpaperActivity.this, getString(R.string.success_applied), Toast.LENGTH_SHORT).show();
            finish();
            showInterstitial();
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
                    saveImage();
                } else {
                    askOrApplyWallpaper();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }
}