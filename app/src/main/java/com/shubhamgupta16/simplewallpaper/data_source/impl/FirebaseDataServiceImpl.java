package com.shubhamgupta16.simplewallpaper.data_source.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.data_source.DataService;
import com.shubhamgupta16.simplewallpaper.data_source.SQLCategories;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

public class FirebaseDataServiceImpl extends DataService {

    final InternalSQLWallpapers sqlWallpapers;
    final Context context;

    public FirebaseDataServiceImpl(Context context, SQLCategories sqlCategories, SQLFav sqlFav) {
        super(sqlCategories, sqlFav);
        this.context = context;
        this.sqlWallpapers = new InternalSQLWallpapers(context);
        InitSQL.applyWallpapers(context, sqlWallpapers, sqlFav);
    }

    @Override
    public void setupCategories(OnCategoriesLoaded onCategoriesLoaded) {
        InitSQL.applyCategories(context, sqlWallpapers, sqlCategories);
        onCategoriesLoaded.onCategoriesLoaded();
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
}
