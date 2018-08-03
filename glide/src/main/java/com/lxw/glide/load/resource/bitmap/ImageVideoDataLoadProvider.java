package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.model.ImageVideoWrapperEncoder;
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
public class ImageVideoDataLoadProvider implements DataLoadProvider<ImageVideoWrapper, Bitmap> {
    private final ImageVideoBitmapDecoder sourceDecoder;
    private final ResourceDecoder<File, Bitmap> cacheDecoder;
    private final ResourceEncoder<Bitmap> encoder;
    private final ImageVideoWrapperEncoder sourceEncoder;

    public ImageVideoDataLoadProvider(DataLoadProvider<InputStream, Bitmap> streamBitmapProvider,
                                      DataLoadProvider<ParcelFileDescriptor, Bitmap> fileDescriptorBitmapProvider) {
        sourceDecoder = new ImageVideoBitmapDecoder(streamBitmapProvider.getSourceDecoder(),
                fileDescriptorBitmapProvider.getSourceDecoder()
        );
        cacheDecoder = streamBitmapProvider.getCacheDecoder();
        encoder = streamBitmapProvider.getEncoder();
        sourceEncoder = new ImageVideoWrapperEncoder(streamBitmapProvider.getSourceEncoder(),
                fileDescriptorBitmapProvider.getSourceEncoder());
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ImageVideoWrapper, Bitmap> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ImageVideoWrapper> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
