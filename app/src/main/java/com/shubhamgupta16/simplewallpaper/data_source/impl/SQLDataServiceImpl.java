package com.shubhamgupta16.simplewallpaper.data_source.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.data_source.DataService;
import com.shubhamgupta16.simplewallpaper.data_source.SQLCategories;
import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

import java.util.ArrayList;

public class SQLDataServiceImpl implements DataService {

    final InternalSQLWallpapers sqlWallpapers;
    final SQLCategories sqlCategories;
    final SQLFav sqlFav;

    public SQLDataServiceImpl(Context context, SQLCategories sqlCategories, SQLFav sqlFav) {
        this.sqlWallpapers = new InternalSQLWallpapers(context);
        this.sqlCategories = sqlCategories;
        this.sqlFav = sqlFav;
        InitSQL.apply(context, sqlWallpapers, sqlCategories, sqlFav);
    }

    @Override
    public ArrayList<CategoryPOJO> getCategories(String query) {
        return sqlCategories.getCategories(query);
    }

    @Override
    public void getWallpapers(int page, DataService.QueryType type, String string, OnWallpapersLoaded onWallpapersLoaded) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            switch (type) {
                case NONE:
                default:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, InternalSQLWallpapers.QueryType.NONE, string));
                    break;
                case CATEGORY:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, InternalSQLWallpapers.QueryType.CATEGORY, string));
                    break;
                case SEARCH:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, InternalSQLWallpapers.QueryType.SEARCH, string));
                    break;
                case FAVORITE:
                    onWallpapersLoaded.onWallpapersLoaded(sqlFav.getWallpapers(page));
                    break;
            }
        }, type == QueryType.FAVORITE ? 200 : 800);

    }

    @Override
    public void toggleFavorite(WallsPOJO wallsPojo, boolean favorite) {
        sqlFav.toggleFavorite(wallsPojo, favorite);
    }

    @Override
    public boolean isFavorite(String url) {
        return sqlFav.isFavorite(url);
    }
}
