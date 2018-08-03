package com.lxw.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.resource.file.FileToStreamDecoder;
import com.lxw.glide.provider.DataLoadProvider;

import java.io.File;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class ImageVideoGifDrawableLoadProvider
        implements DataLoadProvider<ImageVideoWrapper, GifBitmapWrapper> {
    private final ResourceDecoder<File, GifBitmapWrapper> cacheDecoder;
    private final ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> sourceDecoder;
    private final ResourceEncoder<GifBitmapWrapper> encoder;
    private final Encoder<ImageVideoWrapper> sourceEncoder;

    public ImageVideoGifDrawableLoadProvider(DataLoadProvider<ImageVideoWrapper, Bitmap> bitmapProvider,
                                              BitmapPool bitmapPool) {
        final GifBitmapWrapperResourceDecoder decoder = new GifBitmapWrapperResourceDecoder(
                bitmapProvider.getSourceDecoder(),
                bitmapPool
        );
        this.cacheDecoder = new FileToStreamDecoder<>(new GifBitmapWrapperStreamResourceDecoder(decoder));
        this.sourceDecoder = decoder;
        this.encoder = new GifBitmapWrapperResourceEncoder(bitmapProvider.getEncoder());
        this.sourceEncoder = bitmapProvider.getSourceEncoder();
    }

    @Override
    public ResourceDecoder<File, GifBitmapWrapper> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ImageVideoWrapper> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<GifBitmapWrapper> getEncoder() {
        return encoder;
    }
}
