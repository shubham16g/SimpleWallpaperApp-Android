package com.shubhamgupta16.simplewallpaper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.fragments.WallsFragment;

public class MoreWallsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_walls);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        handleAd();

        Intent intent = getIntent();
        FragmentManager manager = getSupportFragmentManager();
        WallsFragment wallsFragment = (WallsFragment) manager.findFragmentById(R.id.moreFragment);
        if (intent.hasExtra("category")) {
            String category = intent.getStringExtra("category");
            toolbar.setTitle(category);
            assert wallsFragment != null;
            wallsFragment.setFragment(SQLHelper.TYPE_CATEGORY, category);
        } else {
            finish();
        }

    }

    private void handleAd() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}