package com.shubhamgupta16.simplewallpaper.data_source;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;

import java.util.ArrayList;

public class SQLCategories extends SQLiteOpenHelper {
    public static final String DB_NAME = "itsCategories";
    public static final String CATEGORIES = "categories";


    public SQLCategories(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORIES + "(name VARCHAR PRIMARY KEY, preview1 VARCHAR, preview2 VARCHAR, preview3 VARCHAR, wallsCount INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CATEGORIES, null, null);

    }

    public void insertCategory(CategoryPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("preview1", pojo.getPreview1());
        contentValues.put("preview2", pojo.getPreview2());
        contentValues.put("preview3", pojo.getPreview3());
        contentValues.put("wallsCount", pojo.getWallsCount());
        if (checkAvailableCategory(pojo.getName())) {
            db.update(CATEGORIES, contentValues, "name='" + pojo.getName() + "'", null);
        } else {
            contentValues.put("name", pojo.getName());
            db.insert(CATEGORIES, null, contentValues);
        }
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
            list.add(new CategoryPOJO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)));
        }
        return list;
    }

}