package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.engine.DecodeFormat;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.resource.NullEncoder;
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
public class FileDescriptorBitmapDataLoadProvider
        implements DataLoadProvider<ParcelFileDescriptor, Bitmap> {
    private final ResourceDecoder<File, Bitmap> cacheDecoder;
    private final FileDescriptorBitmapDecoder sourceDecoder;
    private final BitmapEncoder encoder;
    private final Encoder<ParcelFileDescriptor> sourceEncoder;

    public FileDescriptorBitmapDataLoadProvider(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        cacheDecoder = new FileToStreamDecoder<Bitmap>(new StreamBitmapDecoder(bitmapPool, decodeFormat));
        sourceDecoder =new FileDescriptorBitmapDecoder(bitmapPool,decodeFormat);
        encoder = new BitmapEncoder();
        sourceEncoder = NullEncoder.get();
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ParcelFileDescriptor, Bitmap> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ParcelFileDescriptor> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
