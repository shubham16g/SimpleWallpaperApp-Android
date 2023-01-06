package com.shubhamgupta16.simplewallpaper.models;

import com.google.android.gms.ads.nativead.NativeAd;

import java.io.Serializable;

public class WallsPOJO implements Serializable {
    private final int id;
    private final String name, previewUrl, url, categories;
    private final boolean premium;
    private NativeAd nativeAd;

    public WallsPOJO(int id, String name, String previewUrl, String url, String categories, boolean premium) {
        this.id = id;
        this.name = name;
        this.previewUrl = previewUrl;
        this.url = url;
        this.categories = categories;
        this.premium = premium;
        nativeAd = null;
    }

    public WallsPOJO(NativeAd nativeAd){
        id = -2;
        this.name = null;
        this.previewUrl = null;
        this.url = null;
        this.categories = null;
        this.premium = false;
        this.nativeAd = nativeAd;
    }
    public WallsPOJO(){
        id = -1;
        this.name = null;
        this.previewUrl = null;
        this.url = null;
        this.categories = null;
        this.premium = false;
        this.nativeAd = null;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public int getId() {
        return id;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isPremium() {
        return premium;
    }

    public String getCategories() {
        return categories;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }
}
