package com.lxw.glide.load.resource.transcode;

import android.graphics.Bitmap;

import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.lxw.glide.load.resource.drawable.GlideDrawable;
import com.lxw.glide.load.resource.gifbitmap.GifBitmapWrapper;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class GifBitmapWrapperDrawableTranscoder implements ResourceTranscoder<GifBitmapWrapper,GlideDrawable> {
    private final ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder;

    public GifBitmapWrapperDrawableTranscoder(
            ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder) {
        this.bitmapDrawableResourceTranscoder = bitmapDrawableResourceTranscoder;
    }
    @Override
    public Resource<GlideDrawable> transcode(Resource<GifBitmapWrapper> toTranscode) {
        GifBitmapWrapper gifBitmap = toTranscode.get();
        Resource<Bitmap> bitmapResource = gifBitmap.getBitmapResource();
        //如果Resource<Bitmap>为空，那么说明此时加载的是GIF图，
        // 直接调用getGifResource()方法将图片取出即可，
        // 因为Glide用于加载GIF图片是使用的GifDrawable这个类，
        // 它本身就是一个Drawable对象了。而如果Resource<Bitmap>不为空，
        // 那么就需要再做一次转码，将Bitmap转换成Drawable对象才行，
        // 因为要保证静图和动图的类型一致性，不然逻辑上是不好处理的。
        final Resource<? extends GlideDrawable> result;
        if (bitmapResource != null) {
            result = bitmapDrawableResourceTranscoder.transcode(bitmapResource);
        } else {
            result = gifBitmap.getGifResource();
        }
        //现在再返回到GifBitmapWrapperDrawableTranscoder的transcode()方法中，
        // 你会发现它们的类型就一致了。因为不管是静图的Resource<GlideBitmapDrawable>对象，
        // 还是动图的Resource<GifDrawable>对象，
        // 它们都是属于父类Resource<GlideDrawable>对象的。
        // 因此transcode()方法也是直接返回了Resource<GlideDrawable>，
        // 而这个Resource<GlideDrawable>其实也就是转换过后的Resource<Z>了。
        // This is unchecked but always safe, anything that extends a Drawable can be safely cast to a Drawable.
        return (Resource<GlideDrawable>) result;

    }

    @Override
    public String getId() {
        return "GifBitmapWrapperDrawableTranscoder com.lxw.glide.load.resource.transcode";
    }
}
