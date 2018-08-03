package com.lxw.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;

import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.resource.bitmap.ImageHeaderParser;
import com.lxw.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.lxw.glide.util.ByteArrayPool;

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
public class GifBitmapWrapperResourceDecoder
        implements ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> {
    private static final ImageTypeParser DEFAULT_PARSER = new ImageTypeParser();
    private static final BufferedStreamFactory DEFAULT_STREAM_FACTORY = new BufferedStreamFactory();

    static final int MARK_LIMIT_BYTES = 2048;
    private final ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder;
    private final BitmapPool bitmapPool;
    private final ImageTypeParser parser;
    private final BufferedStreamFactory streamFactory;
    private String id;

    public GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder,BitmapPool bitmapPool) {
        this(bitmapDecoder,  bitmapPool, DEFAULT_PARSER, DEFAULT_STREAM_FACTORY);
    }

    public GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder,
                                           BitmapPool bitmapPool, ImageTypeParser parser, BufferedStreamFactory streamFactory) {
        this.bitmapDecoder = bitmapDecoder;
        this.bitmapPool = bitmapPool;
        this.parser = parser;
        this.streamFactory = streamFactory;
    }

    @Override
    public Resource<GifBitmapWrapper> decode(ImageVideoWrapper source, int width, int height) throws Exception {
        ByteArrayPool pool = ByteArrayPool.get();
        byte[] tempBytes = pool.getBytes();
        GifBitmapWrapper wrapper = null;
        try {
            wrapper = decode(source, width, height, tempBytes);
        } finally {
            pool.releaseBytes(tempBytes);
        }

        return wrapper != null ? new GifBitmapWrapperResource(wrapper) : null;
    }

    private GifBitmapWrapper decode(ImageVideoWrapper source, int width, int height, byte[] tempBytes) throws Exception {
        final GifBitmapWrapper result;
        if (source.getStreamData() != null) {
            result = decodeStream(source, width, height, tempBytes);
        } else {
            result = decodeBitmapWrapper(source, width, height);
        }
        return result;
    }


    private GifBitmapWrapper decodeStream(ImageVideoWrapper source, int width, int height, byte[] bytes) throws Exception {
        InputStream bis = streamFactory.build(source.getStreamData(), bytes);
        bis.mark(MARK_LIMIT_BYTES);
        ImageHeaderParser.ImageType type = parser.parse(bis);
        bis.reset();
        GifBitmapWrapper result = null;
        if (type == ImageHeaderParser.ImageType.GIF) {
//            result = decodeGifWrapper(bis, width, height);
        }
        //如果不是gif，则是 bitmap
        // Decoding the gif may fail even if the type matches.
        if (result == null) {
            // We can only reset the buffered InputStream, so to start from the beginning of the stream, we need to
            // pass in a new source containing the buffered stream rather than the original stream.
            ImageVideoWrapper forBitmapDecoder = new ImageVideoWrapper(bis, source.getFileDescriptor());
            // result GifBitmapWrapper
            result = decodeBitmapWrapper(forBitmapDecoder, width, height);
        }
        return result;
    }

    private GifBitmapWrapper decodeBitmapWrapper(ImageVideoWrapper toDecode, int width, int height) throws Exception {
        GifBitmapWrapper result = null;
        /// BitmapResource
        Resource<Bitmap> bitmapResource = bitmapDecoder.decode(toDecode, width, height);
        if (bitmapResource != null) {
            // BitmapResource再包一层
            result = new GifBitmapWrapper(null, bitmapResource);
        }
        return result;
    }


    @Override
    public String getId() {
        return "GifBitmapWrapperResourceDecoder com.lxw.glide.load.resource.gifbitmap";
    }


    static class BufferedStreamFactory {
        public InputStream build(InputStream is, byte[] buffer) {
            return new RecyclableBufferedInputStream(is, buffer);
        }
    }

    // Visible for testing.
    static class ImageTypeParser {
        public ImageHeaderParser.ImageType parse(InputStream is) throws IOException {
            return new ImageHeaderParser(is).getType();
        }
    }
}
