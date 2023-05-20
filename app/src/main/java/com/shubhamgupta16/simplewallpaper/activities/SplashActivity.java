package com.shubhamgupta16.simplewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.MainApplication;
import com.shubhamgupta16.simplewallpaper.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Within SPLASH_SCREEN_TIMEOUT, redirect to MainActivity if ad not shown
        MainApplication.getDataService(getApplication()).setupCategories(this::startMainActivity);
        handler.postDelayed(runnable, getResources().getInteger(R.integer.splash_screen_timeout));
    }

    private final Runnable runnable = this::startMainActivity;
    private int i = 0;

    public void startMainActivity() {
        if (i == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        }
        i++;
    }

    public void notifyAdShown() {
        // don't redirect automatically
        handler.removeCallbacks(runnable);
    }

    public void notifyAdComplete() {
        startMainActivity();
    }
}