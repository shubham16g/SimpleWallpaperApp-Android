package com.cleanwallpaper.app.models;

import java.io.Serializable;

public class WallsPOJO implements Serializable {
    private String name, previewUrl, url, categories;
    boolean favorite;

    public WallsPOJO(String name, String previewUrl, String url, String categories, boolean favorite) {
        this.name = name;
        this.previewUrl = previewUrl;
        this.url = url;
        this.categories = categories;
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

    public String getCategories() {
        return categories;
    }

}
