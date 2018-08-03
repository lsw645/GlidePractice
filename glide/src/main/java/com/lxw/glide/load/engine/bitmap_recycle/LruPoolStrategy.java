package com.lxw.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/03
 *     desc   :
 * </pre>
 */
public interface LruPoolStrategy {
    void put(Bitmap bitmap);


    Bitmap get(int width, int height, Bitmap.Config config);

    Bitmap removeLast();

    String logBitmap(Bitmap bitmap);

    String logBitmap(int width, int height, Bitmap.Config config);

    int getSize(Bitmap bitmap);
}
