package com.shubhamgupta16.simplewallpaper.data_source;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public interface DataService {

    int PER_PAGE_ITEM = 16;

    enum QueryType {
        NONE,
        CATEGORY,
        FAVORITE,
        SEARCH
    }

    ArrayList<CategoryPOJO> getCategories(String query);

    void getWallpapers(int page, DataService.QueryType type, String string, OnWallpapersLoaded onWallpapersLoaded);

    void toggleFavorite(WallsPOJO wallId, boolean favorite);

    boolean isFavorite(String url);

    interface OnWallpapersLoaded {
        void onWallpapersLoaded(ArrayList<WallsPOJO> list);
    }

}
