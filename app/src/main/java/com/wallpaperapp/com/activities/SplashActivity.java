package com.cleanwallpaper.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.cleanwallpaper.app.models.CategoryPOJO;
import com.cleanwallpaper.app.R;
import com.cleanwallpaper.app.utils.SQLHelper;
import com.cleanwallpaper.app.models.WallsPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SplashActivity extends AppCompatActivity {

    private SQLHelper sqlHelper;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initInterstitial();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1000);
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
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        InterstitialAd.load(this, getString(R.string.splash_interstitial_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
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
                        object.getString("name"),
                        object.getString("previewUrl"),
                        object.getString("url"),
                        object.getString("categories"),
                        false
                );
                sqlHelper.insertWallpaper(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readJSONFromAsset(String fileName) {
        String json = null;
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