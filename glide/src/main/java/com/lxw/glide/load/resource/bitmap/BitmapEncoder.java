package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;

import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.engine.Resource;

import java.io.OutputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class BitmapEncoder implements ResourceEncoder<Bitmap> {
    private static final String TAG = "BitmapEncoder";
    private static final int DEFAULT_COMPRESSION_QUALITY = 90;
    private Bitmap.CompressFormat compressFormat;
    private int quality;

    public BitmapEncoder() {
        this(null, DEFAULT_COMPRESSION_QUALITY);
    }

    public BitmapEncoder(Bitmap.CompressFormat compressFormat, int quality) {
        this.compressFormat = compressFormat;
        this.quality = quality;
    }

    /**
     * 根据 Bitmap的 规格 进行压缩
     * @param resource
     * @param os
     * @return
     */
    @Override
    public boolean encode(Resource<Bitmap> resource, OutputStream os) {
        Bitmap bitmap = resource.get();
        Bitmap.CompressFormat format = getFormat(bitmap);
        bitmap.compress(format, quality, os);
        return true;
    }

    @Override
    public String getId() {
        return "BitmapEncoder.com.lxw.glide.load.resource.bitmap";
    }

    private Bitmap.CompressFormat getFormat(Bitmap bitmap) {
        if (compressFormat != null) {
            return compressFormat;
            //有alpah通道就 png
        } else if (bitmap.hasAlpha()) {
            return Bitmap.CompressFormat.PNG;
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }


}
