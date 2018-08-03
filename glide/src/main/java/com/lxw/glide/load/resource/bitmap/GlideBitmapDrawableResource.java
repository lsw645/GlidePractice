package com.lxw.glide.load.resource.bitmap;


import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.resource.drawable.DrawableResource;
import com.lxw.glide.util.Util;

/**
 */
public class GlideBitmapDrawableResource extends DrawableResource<GlideBitmapDrawable> {
    private final BitmapPool bitmapPool;

    public GlideBitmapDrawableResource(GlideBitmapDrawable drawable, BitmapPool bitmapPool) {
        super(drawable);
        this.bitmapPool = bitmapPool;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(drawable.getBitmap());
    }

    @Override
    public void recycle() {
        bitmapPool.put(drawable.getBitmap());
    }
}
