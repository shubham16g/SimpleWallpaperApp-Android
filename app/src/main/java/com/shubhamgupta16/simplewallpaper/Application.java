package com.shubhamgupta16.simplewallpaper;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.MobileAds;
import com.shubhamgupta16.simplewallpaper.activities.SplashActivity;
import com.shubhamgupta16.simplewallpaper.utils.AppOpenAdManager;
import com.shubhamgupta16.simplewallpaper.utils.InitSQL;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.utils.Utils;


public class Application extends android.app.Application
        implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    private long interstitialShownTime = 0;
    public void interstitialShown(){
        interstitialShownTime = System.currentTimeMillis();
    }
    public boolean canShowInterstitial(){
        return System.currentTimeMillis() > (interstitialShownTime + getResources().getInteger(R.integer.interstitial_show_period));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        Utils.initTheme(this);

        InitSQL initSQL = new InitSQL(this, new SQLHelper(this));

        initSQL.setupCategories();
        initSQL.setupWallpapers();


        MobileAds.initialize(this, initializationStatus -> {
        });

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager(getString(R.string.open_app_ad_id), new AppOpenAdManager.OnAddLoadCallback() {
            @Override
            public void onAdLoaded() {
                tryShowSplashAd();
            }

            @Override
            public void onAdLoadFailed() {

            }

            @Override
            public void onAdShown() {
                if (currentActivity instanceof SplashActivity){
                    ((SplashActivity) currentActivity).notifyAdShown();
                }
            }

            @Override
            public void onAdShowError() {

            }

            @Override
            public void onAdComplete() {
                if (currentActivity instanceof SplashActivity){
                    ((SplashActivity) currentActivity).notifyAdComplete();
                }
            }
        });
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        appOpenAdManager.showAdIfAvailable(currentActivity);

    }

    private void tryShowSplashAd(){
        if (currentActivity instanceof SplashActivity) {
            appOpenAdManager.showAdIfAvailable(currentActivity);
        }
    }

    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

}
