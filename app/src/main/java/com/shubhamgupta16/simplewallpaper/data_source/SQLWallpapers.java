package com.shubhamgupta16.simplewallpaper.data_source;

import static com.shubhamgupta16.simplewallpaper.data_source.DataService.PER_PAGE_ITEM;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public class SQLWallpapers extends SQLiteOpenHelper {
    public static final String DB_NAME = "itsWallpapers";
    public static final String WALLPAPERS = "wallpapers";
    public static final String CATEGORIES = "categories";


    public enum QueryType {
        NONE,
        CATEGORY,
        SEARCH
    }

    public SQLWallpapers(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WALLPAPERS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR, previewUrl VARCHAR, name VARCHAR, categories VARCHAR, premium INTEGER, color VARCHAR, colorCode VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORIES + "(name VARCHAR PRIMARY KEY, preview1 VARCHAR, preview2 VARCHAR, preview3 VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WALLPAPERS, null, null);
        db.delete(CATEGORIES, null, null);

    }

    public boolean isExist(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        boolean isExist = cursor.getCount() > 0;
        cursor.close();

        return isExist;
    }

    public void insertCategory(CategoryPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("preview1", pojo.getPreview1());
        contentValues.put("preview2", pojo.getPreview2());
        contentValues.put("preview3", pojo.getPreview3());
        if (checkAvailableCategory(pojo.getName())) {
            db.update(CATEGORIES, contentValues, "name='" + pojo.getName() + "'", null);
        } else {
            contentValues.put("name", pojo.getName());
            db.insert(CATEGORIES, null, contentValues);
        }
    }

    public void insertWallpaper(WallsPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("previewUrl", pojo.getPreviewUrl());
        contentValues.put("name", pojo.getName());
        contentValues.put("categories", pojo.getCategories());
        contentValues.put("premium", pojo.isPremium() ? 1 : 0);
        if (checkAvailableWallpaper(pojo.getUrl())) {
            db.update(WALLPAPERS, contentValues, "url='" + pojo.getUrl() + "'", null);
        } else {
            contentValues.put("url", pojo.getUrl());
            db.insert(WALLPAPERS, null, contentValues);
        }
    }

    private boolean checkAvailableWallpaper(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT url FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        return c.moveToFirst();
    }

    private boolean checkAvailableCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT name FROM " + CATEGORIES + " WHERE name='" + name + "'", null);
        return c.moveToFirst();
    }

    @SuppressLint("Recycle")
    public ArrayList<CategoryPOJO> getCategories(String query) {
        ArrayList<CategoryPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;
        if (query == null) {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORIES, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORIES + " WHERE name LIKE '%" + query + "%'", null);
        }
        while (cursor.moveToNext()) {
            list.add(new CategoryPOJO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), getWallsInCategoryCount(cursor.getString(0))));
        }
        return list;
    }

    private int getWallsInCategoryCount(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT count(*) FROM " + WALLPAPERS + " WHERE categories LIKE '%" + name + "%'", null);
        int res;
        if (cursor.moveToNext()) {
            res = cursor.getInt(0);
        } else {
            res = 0;
        }
        cursor.close();

        return res;
    }


    public ArrayList<WallsPOJO> getWallpapers(int page, QueryType type, String string) {
        return getListByPages(page, getWhereStatement(type, string));
    }


    private String getWhereStatement(QueryType type, String string) {
        switch (type) {
            case CATEGORY:
                if (string == null) return null;
                return " WHERE categories LIKE '%" + string + "%'";
            case SEARCH:
                if (string == null) return null;
                return " WHERE (name LIKE '%" + string + "%' OR categories LIKE '%" + string + "%')";
            default:
                return "";
        }
    }

    private ArrayList<WallsPOJO> getListByPages(int page, String extras) {
        int offset = (page - 1) * PER_PAGE_ITEM;

        ArrayList<WallsPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS + extras + " LIMIT " + offset + ", " + PER_PAGE_ITEM, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getString(1), cursor.getString(3), cursor.getString(2), cursor.getString(4), cursor.getInt(5) != 0));
        }
        return list;
    }

}