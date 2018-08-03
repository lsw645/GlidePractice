package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.load.model.ImageVideoWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class ImageVideoBitmapDecoder implements ResourceDecoder<ImageVideoWrapper, Bitmap> {
    private static final String TAG = "ImageVideoBitmapDecoder";
    private final ResourceDecoder<InputStream, Bitmap> streamDecoder;
    private final ResourceDecoder<ParcelFileDescriptor, Bitmap> fileDescriptorDecoder;

    public ImageVideoBitmapDecoder(ResourceDecoder<InputStream, Bitmap> streamDecoder,
                                   ResourceDecoder<ParcelFileDescriptor, Bitmap> fileDescriptorDecoder) {
        this.streamDecoder = streamDecoder;
        this.fileDescriptorDecoder = fileDescriptorDecoder;
    }

    @Override
    public Resource<Bitmap> decode(ImageVideoWrapper source, int width, int height) throws Exception {
        Resource<Bitmap> result = null;
        InputStream is = source.getStreamData();
        if (is != null) {
            try {
                result = streamDecoder.decode(is, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            ParcelFileDescriptor fileDescriptor = source.getFileDescriptor();
            if (fileDescriptor != null) {
                try {
                    result = fileDescriptorDecoder.decode(fileDescriptor, width, height);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public String getId() {
        return "ImageVideoBitmapDecoder.com.lxw.glide.load.resource.bitmap";
    }
}
