package com.shubhamgupta16.simplewallpaper;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.MobileAds;
import com.shubhamgupta16.simplewallpaper.activities.SplashActivity;
import com.shubhamgupta16.simplewallpaper.data_source.DataService;
import com.shubhamgupta16.simplewallpaper.data_source.SQLCategories;
import com.shubhamgupta16.simplewallpaper.data_source.impl.SQLDataServiceImpl;
import com.shubhamgupta16.simplewallpaper.utils.AppOpenAdManager;
import com.shubhamgupta16.simplewallpaper.data_source.impl.InitSQL;
import com.shubhamgupta16.simplewallpaper.data_source.impl.SQLWallpapers;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;
import com.shubhamgupta16.simplewallpaper.utils.Utils;


public class MainApplication extends android.app.Application
        implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    private static final String TAG = "MyApplication";

    private int interstitialAfterClicks;
    private int cardClicks;
    private DataService dataService;

    public static DataService getDataService(Application application) {
        return ((MainApplication) application).dataService;
    }

    public void interstitialShown() {
        Log.d(TAG, "interstitialShown: called");
        cardClicks = 0;
    }

    public boolean canShowInterstitial() {
        cardClicks++;
        Log.d(TAG, "canShowInterstitial: " + cardClicks);
        return cardClicks >= interstitialAfterClicks;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        interstitialAfterClicks = getResources().getInteger(R.integer.interstitial_after_clicks);
        cardClicks = interstitialAfterClicks - 2;
        Utils.initTheme(this);

        SQLWallpapers sqlWallpapers = new SQLWallpapers(this);
        SQLCategories sqlCategories = new SQLCategories(this);
        SQLFav sqlFav = new SQLFav(this);
        dataService = new SQLDataServiceImpl(sqlWallpapers, sqlCategories, sqlFav);
        InitSQL.apply(this, sqlWallpapers, sqlCategories, sqlFav);


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
                if (currentActivity instanceof SplashActivity) {
                    ((SplashActivity) currentActivity).notifyAdShown();
                }
            }

            @Override
            public void onAdShowError() {

            }

            @Override
            public void onAdComplete() {
                if (currentActivity instanceof SplashActivity) {
                    ((SplashActivity) currentActivity).notifyAdComplete();
                }
            }
        });
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        appOpenAdManager.showAdIfAvailable(currentActivity);

    }

    private void tryShowSplashAd() {
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
