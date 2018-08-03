package com.lxw.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.load.engine.DecodeFormat;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class VideoBitmapDecoder
        implements BitmapDecoder<ParcelFileDescriptor> {
    private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
    private static int NO_FRAME = -1;
    private MediaMetadataRetrieverFactory factory;
    private int frame;

    public VideoBitmapDecoder() {
        this(DEFAULT_FACTORY, NO_FRAME);
    }

    public VideoBitmapDecoder(int frame) {
        this(DEFAULT_FACTORY, checkValidFrame(frame));
    }

    private static int checkValidFrame(int frame) {
        if (frame < 0) {
            throw new IllegalArgumentException("Requested frame must be non-negative");
        }
        return frame;
    }

    VideoBitmapDecoder(MediaMetadataRetrieverFactory factory) {
        this(factory, NO_FRAME);
    }

    VideoBitmapDecoder(MediaMetadataRetrieverFactory factory, int frame) {
        this.factory = factory;
        this.frame = frame;
    }

    /*
     * 大致理解 ParcelFileDescriptor为支持经常通信的
     * 通过FileDescriptor，使用MediaMetadataRetriever去解码图片或者视频帧
     */
    @Override
    public Bitmap decode(ParcelFileDescriptor resource, BitmapPool bitmapPool, int outWidth, int outHeight, DecodeFormat decodeFormat) throws Exception {
        MediaMetadataRetriever metadataRetriever = factory.build();
        metadataRetriever.setDataSource(resource.getFileDescriptor());
        Bitmap result;
        if (frame >= 0) {
            result = metadataRetriever.getFrameAtTime(frame);
        } else {
            result = metadataRetriever.getFrameAtTime();
        }
        metadataRetriever.release();
        resource.close();
        return result;
    }

    @Override
    public String getId() {
        return "VideoBitmapDecoder.com.lxw.glide.load.resource.bitmap";
    }

    static class MediaMetadataRetrieverFactory {
        public MediaMetadataRetriever build() {
            return new MediaMetadataRetriever();
        }
    }
}
