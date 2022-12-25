package com.shubhamgupta16.simplewallpaper.models;

import java.io.Serializable;

public class WallsPOJO implements Serializable {
    private final int id;
    private final String name, previewUrl, url, categories;
    private final boolean premium;

    public WallsPOJO(int id, String name, String previewUrl, String url, String categories, boolean premium) {
        this.id = id;
        this.name = name;
        this.previewUrl = previewUrl;
        this.url = url;
        this.categories = categories;
        this.premium = premium;
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

}
