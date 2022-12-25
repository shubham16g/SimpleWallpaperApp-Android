package com.shubhamgupta16.simplewallpaper.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.shubhamgupta16.simplewallpaper.activities.WallpaperActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class WallpaperSetter extends AsyncTask<Integer, Integer, Boolean> {

    private final WeakReference<Context> contextRef;
    private final Bitmap bitmap;

    public WallpaperSetter(Context context, Bitmap bitmap) {
        super();
        this.contextRef = new WeakReference<>(context);
        this.bitmap = bitmap;
    }

    private Context getContext() {
        return contextRef.get();
    }

    public static void apply(Context context, Bitmap bitmap, int where, OnWallpaperApplied onWallpaperApplied){
        new WallpaperSetter(context,bitmap){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (onWallpaperApplied != null)
                    onWallpaperApplied.onTaskCompleted(aBoolean);
            }
        }.execute(where);
    }

    private static Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        WallpaperManager manager = WallpaperManager.getInstance(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (integers[0] == 1) {
                try {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (integers[0] == 2) {
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
            if (integers[0] == 0) {
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

    public interface OnWallpaperApplied{
        void onTaskCompleted(boolean b);
    }
}