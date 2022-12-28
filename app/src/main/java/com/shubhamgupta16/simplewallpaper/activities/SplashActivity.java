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
        setupWallpapers();
        setupCategories();


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

    private void setupCategories() {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset("categories"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                CategoryPOJO pojo = new CategoryPOJO(
                        object.getString("name"),
                        object.getString("preview1"),
                        object.getString("preview2"),
                        object.getString("preview3"),
                        0);
                sqlHelper.insertCategory(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupWallpapers() {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset("wallpapers"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                WallsPOJO pojo = new WallsPOJO(
                        0,
                        object.getString("name"),
                        object.getString("previewUrl"),
                        object.getString("url"),
                        object.getString("categories"),
                        object.optBoolean("premium", false)
                );
                sqlHelper.insertWallpaper(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String readJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}