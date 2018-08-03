package com.lxw.glide.load.engine.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class MemorySizeCalculator {
    private static final String TAG = "MemorySizeCalculator";

    // Visible for testing.
    static final int BYTES_PER_ARGB_8888_PIXEL = 4;
    static final int MEMORY_CACHE_TARGET_SCREENS = 2;
    static final int BITMAP_POOL_TARGET_SCREENS = 4;
    static final float MAX_SIZE_MULTIPLIER = 0.4f;

    private final int bitmapPoolSize;
    private final int memoryCacheSize;
    private final Context context;

    public MemorySizeCalculator(Context context) {
        this(context,
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE),
                context.getResources().getDisplayMetrics());
    }

    public MemorySizeCalculator(Context context, ActivityManager activityManager, DisplayMetrics displayMetrics) {
        this.context = context;
        final int maxSize = getMaxSize(activityManager);
        final int screenSize = displayMetrics.widthPixels * displayMetrics.heightPixels
                * BYTES_PER_ARGB_8888_PIXEL;
        int targetPoolSize = screenSize * BITMAP_POOL_TARGET_SCREENS;
        int targetMemoryCacheSize = screenSize * MEMORY_CACHE_TARGET_SCREENS;
        if (targetPoolSize + targetMemoryCacheSize < maxSize) {
            bitmapPoolSize = targetPoolSize;
            memoryCacheSize = targetMemoryCacheSize;
        } else {
            int part = Math.round((float) maxSize / (BITMAP_POOL_TARGET_SCREENS + MEMORY_CACHE_TARGET_SCREENS));
            memoryCacheSize = part * MEMORY_CACHE_TARGET_SCREENS;
            bitmapPoolSize = part * BITMAP_POOL_TARGET_SCREENS;
        }
    }

    public int getBitmapPoolSize() {
        return bitmapPoolSize;
    }

    public int getMemoryCacheSize() {
        return memoryCacheSize;
    }

    private static int getMaxSize(ActivityManager activityManager) {
        final int memoryClassBytes = activityManager.getMemoryClass() * 1024 * 1024;
        return Math.round(memoryClassBytes * MAX_SIZE_MULTIPLIER);
    }
}
