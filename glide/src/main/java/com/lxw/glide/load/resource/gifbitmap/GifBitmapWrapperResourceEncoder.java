package com.lxw.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;

import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.engine.Resource;

import java.io.OutputStream;

/**

 */
public class GifBitmapWrapperResourceEncoder implements ResourceEncoder<GifBitmapWrapper> {
    private final ResourceEncoder<Bitmap> bitmapEncoder;

    private String id;

    public GifBitmapWrapperResourceEncoder(ResourceEncoder<Bitmap> bitmapEncoder
                                          ) {
        this.bitmapEncoder = bitmapEncoder;
    }

    @Override
    public boolean encode(Resource<GifBitmapWrapper> resource, OutputStream os) {
        final GifBitmapWrapper gifBitmap = resource.get();
        final Resource<Bitmap> bitmapResource = gifBitmap.getBitmapResource();

        if (bitmapResource != null) {
            return bitmapEncoder.encode(bitmapResource, os);
        }
        return  false;
    }

    @Override
    public String getId() {
        if (id == null) {
            id = bitmapEncoder.getId() ;
        }
        return id;
    }
}
