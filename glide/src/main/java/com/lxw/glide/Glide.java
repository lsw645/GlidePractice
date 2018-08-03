package com.lxw.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.lxw.glide.load.engine.DecodeFormat;
import com.lxw.glide.load.engine.Engine;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.engine.cache.MemoryCache;
import com.lxw.glide.load.model.GenericLoaderFactory;
import com.lxw.glide.load.model.GlideUrl;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.model.ModelLoaderFactory;
import com.lxw.glide.load.model.file_descriptor.FileDescriptorStringLoader;
import com.lxw.glide.load.model.file_descriptor.FileDescriptorUriLoader;
import com.lxw.glide.load.model.stream.HttpUrlGlideUrlLoader;
import com.lxw.glide.load.model.stream.StreamStringLoader;
import com.lxw.glide.load.model.stream.StreamUriLoader;
import com.lxw.glide.load.resource.bitmap.FileDescriptorBitmapDataLoadProvider;
import com.lxw.glide.load.resource.bitmap.ImageVideoDataLoadProvider;
import com.lxw.glide.load.resource.bitmap.StreamBitmapDataLoadProvider;
import com.lxw.glide.load.resource.drawable.GlideDrawable;
import com.lxw.glide.load.resource.file.StreamFileDataLoadProvider;
import com.lxw.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.lxw.glide.load.resource.gifbitmap.ImageVideoGifDrawableLoadProvider;
import com.lxw.glide.load.resource.transcode.GifBitmapWrapperDrawableTranscoder;
import com.lxw.glide.load.resource.transcode.GlideBitmapDrawableTranscoder;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;
import com.lxw.glide.load.resource.transcode.TranscoderRegistry;
import com.lxw.glide.manager.RequestManager;
import com.lxw.glide.manager.RequestManagerRetriever;
import com.lxw.glide.module.GlideModule;
import com.lxw.glide.module.ManifestParser;
import com.lxw.glide.provider.DataLoadProvider;
import com.lxw.glide.provider.DataLoadProviderRegistry;
import com.lxw.glide.request.target.GlideDrawableImageViewTarget;
import com.lxw.glide.request.target.Target;

import java.io.File;
import java.io.InputStream;
import java.util.List;


/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class Glide {
    private static final String TAG = "Glide";
    private static volatile Glide glide;
    private final Engine engine;
    private final BitmapPool bitmapPool;
    private final MemoryCache memoryCache;
    private final DecodeFormat decodeFormat;
    private final Handler mainHandler;
    private final GenericLoaderFactory loaderFactory;
    private final DataLoadProviderRegistry dataLoadProviderRegistry;
    private final TranscoderRegistry transcoderRegistry;

    public Glide(Engine engine, MemoryCache memoryCache, Context context, BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this.engine = engine;
        this.memoryCache = memoryCache;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;

        loaderFactory = new GenericLoaderFactory(context);
        mainHandler = new Handler(Looper.getMainLooper());
        transcoderRegistry = new TranscoderRegistry();
        dataLoadProviderRegistry = new DataLoadProviderRegistry();
        {
            StreamBitmapDataLoadProvider streamBitmapDataLoadProvider =
                    new StreamBitmapDataLoadProvider(bitmapPool, decodeFormat);

            dataLoadProviderRegistry.register(InputStream.class, Bitmap.class,
                    streamBitmapDataLoadProvider);

            FileDescriptorBitmapDataLoadProvider fileDescriptorBitmapDataLoadProvider =
                    new FileDescriptorBitmapDataLoadProvider(bitmapPool, decodeFormat);

            dataLoadProviderRegistry.register(ParcelFileDescriptor.class, Bitmap.class,
                    fileDescriptorBitmapDataLoadProvider);

            ImageVideoDataLoadProvider imageVideoDataLoadProvider =
                    new ImageVideoDataLoadProvider(streamBitmapDataLoadProvider,
                            fileDescriptorBitmapDataLoadProvider);
            dataLoadProviderRegistry.register(ImageVideoWrapper.class, Bitmap.class, imageVideoDataLoadProvider);

            dataLoadProviderRegistry.register(ImageVideoWrapper.class, GifBitmapWrapper.class,
                    new ImageVideoGifDrawableLoadProvider(imageVideoDataLoadProvider, bitmapPool));
            dataLoadProviderRegistry.register(InputStream.class, File.class, new StreamFileDataLoadProvider());
        }


        // 花了一大堆时间看这个Factory ，其实Factory的作用起的就是懒加载
        register(String.class, ParcelFileDescriptor.class, new FileDescriptorStringLoader.Factory());
        register(String.class, InputStream.class, new StreamStringLoader.Factory());
        register(Uri.class, InputStream.class, new StreamUriLoader.Factory());
        register(GlideUrl.class, InputStream.class, new HttpUrlGlideUrlLoader.Factory());
        register(Uri.class, ParcelFileDescriptor.class, new FileDescriptorUriLoader.Factory());
//        transcoderRegistry.register(Bitmap.class,GlideBitmapDrawable.class,
//                new GlideBitmapDrawableTranscoder(context.getResources(),bitmapPool));
        transcoderRegistry.register(GifBitmapWrapper.class, GlideDrawable.class,
                new GifBitmapWrapperDrawableTranscoder(
                        new GlideBitmapDrawableTranscoder(context.getResources(), bitmapPool)));
    }


    public static Glide get(Context context) {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    Context applicationContext = context.getApplicationContext();
                    List<GlideModule> modules = new ManifestParser(applicationContext).parse();
                    GlideBuilder builder = new GlideBuilder(applicationContext);
                    //先配置options
                    for (GlideModule module : modules) {
                        module.applyOptions(applicationContext, builder);
                    }
                    glide = builder.createGlide();
                    //再进行注册
                    for (GlideModule module : modules) {
                        module.registerComponents(applicationContext, glide);
                    }
                }
            }
        }
        return glide;
    }

    public static RequestManager with(FragmentActivity activity) {
        RequestManagerRetriever retriever = RequestManagerRetriever.get();
        return retriever.get(activity);
    }

    public <TranscodeType> Target buildImageViewTarget(ImageView view, Class<TranscodeType> clazz) {
        if (GlideDrawable.class.isAssignableFrom(clazz)) {
            return new GlideDrawableImageViewTarget(view);
        }
        return null;
    }

    public <ModelType, ResourceType> void register(
            Class<ModelType> modelType, Class<ResourceType> resourceType, ModelLoaderFactory<ModelType, ResourceType> modelLoaderFactory) {
        ModelLoaderFactory<ModelType, ResourceType> removed = loaderFactory.register(modelType, resourceType, modelLoaderFactory);
        if (removed != null) {
            removed.teardown();
        }
    }

    public <ModelType, ResourceType> void unregister(Class<ModelType> model, Class<ResourceType> resourceType) {
        ModelLoaderFactory<ModelType, ResourceType> removed = loaderFactory.unregister(model, resourceType);
        if (removed != null) {
            removed.teardown();
        }
    }

    private GenericLoaderFactory getLoaderFactory() {
        return loaderFactory;
    }

    public static <T, Y> ModelLoader<T, Y> buildModelLoader(Class<T> modelClass, Class<Y> resourceClass,
                                                            Context context) {
        if (modelClass == null) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Unable to load null model, setting placeholder only");
            }
            return null;
        }
        return Glide.get(context).getLoaderFactory().buildModelLoader(modelClass, resourceClass);
    }

    public <TranscodeType, ResourceType> ResourceTranscoder<ResourceType, TranscodeType> buildTranscoder(Class<ResourceType> resource, Class<TranscodeType> transcode) {
        return transcoderRegistry.get(resource,transcode);
    }

    public <DataType, TranscodeType> DataLoadProvider<DataType, TranscodeType> buildDataProvider(
            Class<DataType> dataClass,
            Class<TranscodeType> transcodeClass) {
        return dataLoadProviderRegistry.get(dataClass, transcodeClass);
    }

    public Engine getEngine() {
        return engine;
    }

    public BitmapPool getBitmapPool() {
        return bitmapPool;
    }


}
