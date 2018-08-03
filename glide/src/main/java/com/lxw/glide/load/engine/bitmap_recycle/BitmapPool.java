package com.lxw.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public interface BitmapPool {

    boolean put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    void clearMemory();
    Bitmap getDirty(int width, int height, Bitmap.Config config);
    void trimMemory(int level);

    /**
     * Returns the current maximum size of the pool in bytes.
     */
    int getMaxSize();

    /**
     * Multiplies the initial size of the pool by the given multipler to dynamically and synchronously allow users to
     * adjust the size of the pool.
     *
     * <p>
     *     If the current total size of the pool is larger than the max size after the given multiplier is applied,
     *     {@link Bitmap}s should be evicted until the pool is smaller than the new max size.
     * </p>
     *
     * @param sizeMultiplier The size multiplier to apply between 0 and 1.
     */
    void setSizeMultiplier(float sizeMultiplier);
}
