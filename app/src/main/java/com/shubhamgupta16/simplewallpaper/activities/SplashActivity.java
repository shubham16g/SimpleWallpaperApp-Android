package com.shubhamgupta16.simplewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_SECONDS = 5;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Within SPLASH_DURATION_SECONDS, redirect to MainActivity
        handler.postDelayed(runnable, SPLASH_DURATION_SECONDS * 1000);
    }

    private final Runnable runnable = this::startMainActivity;

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    public void notifyAdShown() {
        // don't redirect automatically
        handler.removeCallbacks(runnable);
    }

    public void notifyAdComplete() {
        startMainActivity();
    }
}