package com.shubhamgupta16.simplewallpaper.data_source;

import android.os.Handler;
import android.os.Looper;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

import java.util.ArrayList;

public class SQLDataServiceImpl implements DataService {

    final SQLWallpapers sqlWallpapers;
    final SQLFav sqlFav;

    public SQLDataServiceImpl(SQLWallpapers sqlWallpapers, SQLFav sqlFav) {
        this.sqlWallpapers = sqlWallpapers;
        this.sqlFav = sqlFav;
    }

    @Override
    public ArrayList<CategoryPOJO> getCategories(String query) {
        return sqlWallpapers.getCategories(query);
    }

    @Override
    public void getWallpapers(int page, DataService.QueryType type, String string, OnWallpapersLoaded onWallpapersLoaded) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            switch (type) {
                case NONE:
                default:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, SQLWallpapers.QueryType.NONE, string));
                    break;
                case CATEGORY:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, SQLWallpapers.QueryType.CATEGORY, string));
                    break;
                case SEARCH:
                    onWallpapersLoaded.onWallpapersLoaded(sqlWallpapers.getWallpapers(page, SQLWallpapers.QueryType.SEARCH, string));
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
                    onPagesCountLoaded.onPagesCountLoaded(sqlWallpapers.getPagesCount(SQLWallpapers.QueryType.NONE, string));
                    break;
                case CATEGORY:
                    onPagesCountLoaded.onPagesCountLoaded(sqlWallpapers.getPagesCount(SQLWallpapers.QueryType.CATEGORY, string));
                    break;
                case SEARCH:
                    onPagesCountLoaded.onPagesCountLoaded(sqlWallpapers.getPagesCount(SQLWallpapers.QueryType.SEARCH, string));
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
    public boolean isFavorite(String url) {
        return sqlFav.isFavorite(url);
    }
}
