package com.shubhamgupta16.simplewallpaper.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.Utils;
import com.shubhamgupta16.simplewallpaper.utils.WallpaperSetter;

import java.io.IOException;

public class WallpaperActivity extends AppCompatActivity {

    private Bitmap imageBitmap;
    private PhotoView photoView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private View mDecorView, topShadow, bottomShadow;
    private LinearLayout bottomNavLayout;
    private SQLHelper sqlHelper;
    private WallsPOJO pojo;
    private InterstitialAd mInterstitialAd;
    private boolean isLockedForPremium;


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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

//        toolbar.setTitle(pojo.getName());
//        toolbar.setSubtitle(pojo.getCategories());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initInterstitial();

        if (pojo.isPremium()) {
            isLockedForPremium = true;
            findViewById(R.id.premiumImage).setVisibility(View.VISIBLE);
        } else {
            isLockedForPremium = false;
        }

        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                toggleTouch();
            }
        });

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
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLockedForPremium) {
                    return;
                }
                saveImage();
            }
        });

//        apply button click
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLockedForPremium) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String[] options = {"Home screen", "Lock screen", "Both"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(WallpaperActivity.this);
                    builder.setTitle("Apply on");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            applyWallpaper(which + 1);
                        }
                    });
                    builder.show();
                } else {
                    applyWallpaper(0);
                }
            }
        });
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sqlHelper.isFavorite(pojo.getUrl())) {
                    sqlHelper.toggleFavorite(pojo.getUrl(), false);
                    heartImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                } else {
                    sqlHelper.toggleFavorite(pojo.getUrl(), true);
                    heartImage.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isStoragePermissionGranted())
            return;
        final boolean isSaved = Utils.save(this, imageBitmap, getString(R.string.app_name),
                pojo.getName().replaceAll("\\s", "_"));
        if (isSaved){
            Toast.makeText(this, "Image Saved Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error while saving image.", Toast.LENGTH_SHORT).show();
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
                public void onAnimationStart(Animator animator) {
                    if (bottomNavLayout.getVisibility() == View.VISIBLE) {
                        bottomNavLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (bottomNavLayout.getVisibility() == View.GONE) {
                        bottomNavLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            hideSysUI();
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void applyWallpaper(int where){
        progressBar.setVisibility(View.VISIBLE);
        WallpaperSetter.apply(this, imageBitmap, where, b -> {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(WallpaperActivity.this, WallpaperActivity.class);
            intent.putExtra("pojo", pojo);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Toast.makeText(WallpaperActivity.this, "Successfully Applied.", Toast.LENGTH_SHORT).show();
            finish();
            showInterstitial();
        });
    }
}