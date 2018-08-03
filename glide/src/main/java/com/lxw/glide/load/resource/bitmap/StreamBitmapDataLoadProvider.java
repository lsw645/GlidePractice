package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.engine.DecodeFormat;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.model.StreamEncoder;
import com.lxw.glide.load.resource.file.FileToStreamDecoder;
import com.lxw.glide.provider.DataLoadProvider;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class StreamBitmapDataLoadProvider implements DataLoadProvider<InputStream, Bitmap> {
    private final StreamBitmapDecoder decoder;
    private final BitmapEncoder encoder;
    private final StreamEncoder sourceEncoder;
    private final FileToStreamDecoder<Bitmap> cacheDecoder;

    public StreamBitmapDataLoadProvider(
            BitmapPool bitmapPool,
            DecodeFormat decodeFormat) {
        this.decoder = new StreamBitmapDecoder(bitmapPool,decodeFormat);
        this.encoder = new BitmapEncoder();
        this.sourceEncoder = new StreamEncoder();
        this.cacheDecoder = new FileToStreamDecoder<>(decoder);
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<InputStream, Bitmap> getSourceDecoder() {
        return decoder;
    }

    @Override
    public Encoder<InputStream> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
