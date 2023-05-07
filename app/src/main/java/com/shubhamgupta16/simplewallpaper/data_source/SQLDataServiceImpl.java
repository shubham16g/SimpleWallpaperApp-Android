package com.shubhamgupta16.simplewallpaper.data_source;

import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

import java.util.ArrayList;

public class SQLDataServiceImpl implements DataService {

    final SQLHelper sqlHelper;
    final SQLFav sqlFav;

    public SQLDataServiceImpl(SQLHelper sqlHelper, SQLFav sqlFav) {
        this.sqlHelper = sqlHelper;
        this.sqlFav = sqlFav;
    }

    @Override
    public ArrayList<CategoryPOJO> getCategories(String query) {
        return sqlHelper.getCategories(query);
    }

    @Override
    public void getWallpapers(int page, DataService.QueryType type, String string, OnWallpapersLoaded onWallpapersLoaded) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            switch (type) {
                case NONE:
                default:
                    onWallpapersLoaded.onWallpapersLoaded(sqlHelper.getWallpapers(page, SQLHelper.QueryType.NONE, string));
                    break;
                case CATEGORY:
                    onWallpapersLoaded.onWallpapersLoaded(sqlHelper.getWallpapers(page, SQLHelper.QueryType.CATEGORY, string));
                    break;
                case SEARCH:
                    onWallpapersLoaded.onWallpapersLoaded(sqlHelper.getWallpapers(page, SQLHelper.QueryType.SEARCH, string));
                    break;
                case FAVORITE:
                    onWallpapersLoaded.onWallpapersLoaded(sqlFav.getWallpapers(page));
                    break;
            }
        }, type == QueryType.FAVORITE ? 200 : 800);

    }

    @Override
    public void getPagesCount(DataService.QueryType type, String string, OnPagesCountLoaded onPagesCountLoaded) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            switch (type) {
                case NONE:
                default:
                    onPagesCountLoaded.onPagesCountLoaded(sqlHelper.getPagesCount(SQLHelper.QueryType.NONE, string));
                    break;
                case CATEGORY:
                    onPagesCountLoaded.onPagesCountLoaded(sqlHelper.getPagesCount(SQLHelper.QueryType.CATEGORY, string));
                    break;
                case SEARCH:
                    onPagesCountLoaded.onPagesCountLoaded(sqlHelper.getPagesCount(SQLHelper.QueryType.SEARCH, string));
                    break;
                case FAVORITE:
                    onPagesCountLoaded.onPagesCountLoaded(sqlFav.getPagesCount());
                    break;
            }
        }, type == QueryType.FAVORITE ? 200 : 800);
    }

    @Override
    public void toggleFavorite(WallsPOJO wallId, boolean favorite) {
        sqlFav.toggleFavorite(wallId, favorite);
    }

    @Override
    public boolean isFavorite(int id) {
        return sqlFav.isFavorite(id);
    }
}
