package com.shubhamgupta16.simplewallpaper.data_source;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;


public class PreserveOldFav extends SQLiteOpenHelper {
    private static final String OLD_WALLPAPERS = "wallpapers";
    private static final String DB_NAME = "myWalls2";


    private PreserveOldFav(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void apply(Context context, SQLFav sqlFav) {
        PreserveOldFav oldFav = new PreserveOldFav(context);
        oldFav.preserveOldTable(sqlFav);
        oldFav.close();
    }

    public void preserveOldTable(SQLFav fav) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + OLD_WALLPAPERS + " WHERE favorite=1", null);
            while (cursor.moveToNext()) {
                final WallsPOJO pojo = new WallsPOJO(cursor.getString(1), cursor.getString(3), cursor.getString(2), cursor.getString(4), cursor.getInt(5) != 0);
                fav.toggleFavorite(pojo, true);
            }
            cursor.close();
        } catch (Exception ignored) {
        }
    }
}
