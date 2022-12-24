package com.shubhamgupta16.simplewallpaper.models;

public class CategoryPOJO {
    private final String name, preview1, preview2, preview3;
    private final int wallsCount;

    public CategoryPOJO(String name, String preview1, String preview2, String preview3, int wallsCount) {
        this.name = name;
        this.preview1 = preview1;
        this.preview2 = preview2;
        this.preview3 = preview3;
        this.wallsCount = wallsCount;
    }

    public int getWallsCount() {
        return wallsCount;
    }

    public String getName() {
        return name;
    }

    public String getPreview1() {
        return preview1;
    }

    public String getPreview2() {
        return preview2;
    }

    public String getPreview3() {
        return preview3;
    }
}