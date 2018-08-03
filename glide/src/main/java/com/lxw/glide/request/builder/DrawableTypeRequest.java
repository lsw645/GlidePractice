package com.lxw.glide.request.builder;

import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.ImageVideoModelLoader;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.resource.drawable.GlideDrawable;
import com.lxw.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;
import com.lxw.glide.manager.Lifecycle;
import com.lxw.glide.manager.RequestTracker;
import com.lxw.glide.provider.DataLoadProvider;
import com.lxw.glide.provider.FixedLoadProvider;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class DrawableTypeRequest<ModelType> extends DrawableReuestBuilder<ModelType> {
    private final ModelLoader<ModelType, InputStream> streamModelLoader;
    private final ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader;

    public DrawableTypeRequest(Class<ModelType> modelClass,
                               ModelLoader<ModelType, InputStream> streamModelLoader,
                               ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader,
                               Context context,
                               Glide glide, RequestTracker requestTracker,Lifecycle lifecycle
    ) {
        super(modelClass, context, glide,
                buildProvider(glide, streamModelLoader,
                        fileDescriptorModelLoader,
                        GifBitmapWrapper.class,
                        GlideDrawable.class,
                        null),
                requestTracker, lifecycle);
        this.streamModelLoader = streamModelLoader;
        this.fileDescriptorModelLoader = fileDescriptorModelLoader;
    }

    private static   <ModelType, ResourceType, TranscodeType>
    FixedLoadProvider<ModelType, ImageVideoWrapper, ResourceType, TranscodeType>
    buildProvider(
            Glide glide,
            ModelLoader<ModelType, InputStream> streamModelLoader,
            ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader,
            Class<ResourceType> resourceTypeClass,
            Class<TranscodeType> transcodeTypeClass,
            ResourceTranscoder<ResourceType, TranscodeType> transcoder
    ) {
        if (streamModelLoader == null && fileDescriptorModelLoader == null) {
            return null;
        }
        if (transcoder == null) {
            transcoder = glide.buildTranscoder(resourceTypeClass, transcodeTypeClass);
        }
        //GifBitmapWrapper  ImageVideoGifDrawableLoadProvider
        DataLoadProvider<ImageVideoWrapper, ResourceType> dataLoadProvider = glide.buildDataProvider(ImageVideoWrapper.class, resourceTypeClass);
        ImageVideoModelLoader<ModelType> modelLoader = new ImageVideoModelLoader<ModelType>(streamModelLoader,
                fileDescriptorModelLoader);
        return new FixedLoadProvider<ModelType, ImageVideoWrapper, ResourceType, TranscodeType>(modelLoader, transcoder, dataLoadProvider);
    }


}
