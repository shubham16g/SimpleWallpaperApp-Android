package com.shubhamgupta16.simplewallpaper.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import java.io.IOException;

public class ApplyWallpaper {
    public static boolean fromBitmap(Context context, Bitmap bitmap, int where) {
        WallpaperManager manager = WallpaperManager.getInstance(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (where == 1) {
                try {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (where == 2) {
                try {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                try {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } else {
            if (where == 0) {
                try {
                    manager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}
