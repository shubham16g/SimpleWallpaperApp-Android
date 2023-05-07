package com.shubhamgupta16.simplewallpaper.data_source;

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
    public ArrayList<WallsPOJO> getWallpapers(int page, DataService.QueryType type, String string) {
        switch (type) {
            case NONE:
            default:
                return sqlHelper.getWallpapers(page, SQLHelper.QueryType.NONE, string);
            case CATEGORY:
                return sqlHelper.getWallpapers(page, SQLHelper.QueryType.CATEGORY, string);
            case SEARCH:
                return sqlHelper.getWallpapers(page, SQLHelper.QueryType.SEARCH, string);
            case FAVORITE:
                return sqlFav.getWallpapers(page);
        }
    }

    @Override
    public int getPagesCount(DataService.QueryType type, String string) {
        switch (type) {
            case NONE:
            default:
                return sqlHelper.getPagesCount(SQLHelper.QueryType.NONE, string);
            case CATEGORY:
                return sqlHelper.getPagesCount(SQLHelper.QueryType.CATEGORY, string);
            case SEARCH:
                return sqlHelper.getPagesCount(SQLHelper.QueryType.SEARCH, string);
            case FAVORITE:
                return sqlFav.getPagesCount();
        }
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
