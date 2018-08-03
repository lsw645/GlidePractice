package com.lxw.glide.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.lxw.glide.Glide;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawableResource;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class GlideBitmapDrawableTranscoder implements ResourceTranscoder<Bitmap, GlideBitmapDrawable> {
    private final BitmapPool bitmapPool;
    private final Resources resources;

    public GlideBitmapDrawableTranscoder(Context context) {
        this(context.getResources(), Glide.get(context).getBitmapPool());
    }

    public GlideBitmapDrawableTranscoder(Resources resources, BitmapPool bitmapPool) {
        this.resources = resources;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<GlideBitmapDrawable> transcode(Resource<Bitmap> toTranscode) {
        GlideBitmapDrawable drawable = new GlideBitmapDrawable(resources, toTranscode.get());
        return new GlideBitmapDrawableResource(drawable, bitmapPool);
    }

    @Override
    public String getId() {
        return "GlideBitmapDrawableTranscoder.com.lxw.glide.load.resource.transcode";
    }
}
