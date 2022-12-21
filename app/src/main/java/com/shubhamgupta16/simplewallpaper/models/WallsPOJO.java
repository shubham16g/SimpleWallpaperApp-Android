package com.shubhamgupta16.simplewallpaper.models;

import java.io.Serializable;

public class WallsPOJO implements Serializable {
    private String name, previewUrl, url, categories;
    boolean premium;
    boolean favorite;

    public WallsPOJO(String name, String previewUrl, String url, String categories, boolean premium, boolean favorite) {
        this.name = name;
        this.previewUrl = previewUrl;
        this.url = url;
        this.categories = categories;
        this.premium = premium;
        this.favorite = favorite;
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

}
