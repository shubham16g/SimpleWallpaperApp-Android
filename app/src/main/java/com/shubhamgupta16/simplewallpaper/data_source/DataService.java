package com.shubhamgupta16.simplewallpaper.data_source;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public interface DataService {

    public ArrayList<CategoryPOJO> getCategories(String query);

//    private int getWallsInCategoryCount(String name);
    public ArrayList<WallsPOJO> getWallpapers(int page, int type, String string);

    public int getPagesCount(int type, String string);

    public void toggleFavorite(int wallId, boolean favorite);

    public boolean isFavorite(int id);

}
