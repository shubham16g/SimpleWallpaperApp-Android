package com.shubhamgupta16.simplewallpaper.data_source;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public interface DataService {

    enum QueryType {
        NONE,
        CATEGORY,
        FAVORITE,
        SEARCH
    }

    ArrayList<CategoryPOJO> getCategories(String query);

    ArrayList<WallsPOJO> getWallpapers(int page, DataService.QueryType type, String string);

    int getPagesCount(DataService.QueryType type, String string);

    void toggleFavorite(WallsPOJO wallId, boolean favorite);

    boolean isFavorite(int id);

}
