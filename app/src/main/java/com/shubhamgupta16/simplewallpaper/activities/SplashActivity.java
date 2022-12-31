package com.shubhamgupta16.simplewallpaper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private SQLHelper sqlHelper;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initInterstitial();

        sqlHelper = new SQLHelper(this);


    }

    private void startApp() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();

    }

    private void showInterstitial() {
        startApp();
        mInterstitialAd.show(this);

    }

    private void initInterstitial() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AtomicBoolean isRedirected = new AtomicBoolean(false);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            isRedirected.set(true);
            startApp();
        };

//        wait for ad to load within 8 seconds, else open main activity
        handler.postDelayed(runnable, 8000);

        InterstitialAd.load(this, getString(R.string.splash_interstitial_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                if (isRedirected.get()) return;
                mInterstitialAd = interstitialAd;
                handler.removeCallbacks(runnable);
                showInterstitial();
            }
        });
    }
}