package com.lxw.glide.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.lxw.glide.Priority;
import com.lxw.glide.load.Key;
import com.lxw.glide.load.data.DataFetcher;
import com.lxw.glide.load.engine.DiskCacheStrategy;
import com.lxw.glide.load.engine.Engine;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;
import com.lxw.glide.provider.LoadProvider;
import com.lxw.glide.request.target.SizeReadyCallback;
import com.lxw.glide.request.target.Target;
import com.lxw.glide.util.LogTime;
import com.lxw.glide.util.Util;

import java.util.Queue;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :  TODO ModelType 数据类型
 *               TODO ImageVideoWrapper 从缓存或网络请求获取图片后 包装成InputStream或FileDescriptor
 *               TODO GifBitmapWrapper  封装这Gif 与Bitmap
 *               TODO GlideDrawable 最后返回的是Drawable类型，个人认为是为了统一封装动画的操作
 * </pre>
 */
public final class GenericRequest<ModelType, DataType, ResourceType, TranscodeType>
        implements Request, SizeReadyCallback, ResourceCallback {

    private static final Queue<GenericRequest<?, ?, ?, ?>> REQUEST_POOL = Util.createQueue(0);
    private Engine.LoadStatus loadStatus;

    public static <TranscodeType, ResourceType, DataType, ModelType> GenericRequest obtain(
            LoadProvider<ModelType, DataType, ResourceType, TranscodeType> loadProvider,
            ModelType model,
            Key signature,
            Context context,
            Priority priority,
            Target<TranscodeType> target, float sizeMultiplier,
            Drawable placeholderDrawable,
            int placeholderId,
            Drawable errorDrawable,
            int errorId,
            RequestListener<? super ModelType, TranscodeType> requestListener,
            Engine engine,
            boolean isCacheable,
            int overrideWidth,
            int overrideHeight,
            DiskCacheStrategy diskCacheStrategy) {
        @SuppressWarnings("unchecked")
        GenericRequest<ModelType, DataType, ResourceType, TranscodeType> request =
                (GenericRequest<ModelType, DataType, ResourceType, TranscodeType>) REQUEST_POOL.poll();
        if (request == null) {
            request = new GenericRequest<ModelType, DataType, ResourceType, TranscodeType>();
        }
        request.init(loadProvider,
                model,
                signature,
                context,
                target,
                priority,
                sizeMultiplier,
                placeholderDrawable,
                placeholderId,
                errorDrawable,
                errorId,
                requestListener,
                engine,
                isCacheable,
                overrideWidth,
                overrideHeight,
                diskCacheStrategy);
        return request;
    }

    private void init(LoadProvider<ModelType, DataType, ResourceType, TranscodeType> loadProvider,
                      ModelType model,
                      Key signature,
                      Context context,
                      Target<TranscodeType> target, Priority priority,
                      float sizeMultiplier,
                      Drawable placeholderDrawable,
                      int placeholderId,
                      Drawable errorDrawable,
                      int errorId,
                      RequestListener<? super ModelType, TranscodeType> requestListener,
                      Engine engine,
                      boolean isMemoryCacheable,
                      int overrideWidth,
                      int overrideHeight,
                      DiskCacheStrategy diskCacheStrategy) {
        this.loadProvider = loadProvider;
        this.model = model;
        this.signature = signature;
        this.context = context;
        this.target = target;
        this.priority = priority;
        this.sizeMultiplier = sizeMultiplier;
        this.placeholderDrawable = placeholderDrawable;
        this.placeholderResourceId = placeholderId;
        this.errorDrawable = errorDrawable;
        this.errorResourceId = errorId;
        this.requestListener = requestListener;
        this.engine = engine;
        this.isMemoryCacheable = isMemoryCacheable;
        this.overrideWidth = overrideWidth;
        this.overrideHeight = overrideHeight;
        this.diskCacheStrategy = diskCacheStrategy;
        status = Status.PENDING;
    }

    private enum Status {
        //即将
        PENDING,
        //
        RUNNING,

        WAITING_FOR_SIZE,

        COMPLETE,

        FAILED,

        CANCELLED,

        CLEARED,

        PAUSED,
    }

    private final String tag = String.valueOf(hashCode());
    private float sizeMultiplier;
    private Key signature;
    private int placeholderResourceId;
    private int errorResourceId;
    private Context context;
    private ModelType model;
    private boolean isMemoryCacheable;
    private Priority priority;
    private Class<TranscodeType> transcodeClass;
    private Target<TranscodeType> target;
    private RequestListener<? super ModelType, TranscodeType> requestListener;
    private Engine engine;
    private int overrideWidth;
    private int overrideHeight;
    private DiskCacheStrategy diskCacheStrategy;
    //处理
    private LoadProvider<ModelType, DataType, ResourceType, TranscodeType> loadProvider;
    private Drawable placeholderDrawable;
    private Drawable errorDrawable;
    private boolean loadedFromMemoryCache;
    private Resource<?> resource;
    private long startTime;
    private Status status;

    @Override
    public void begin() {
        startTime = LogTime.getLogTime();
        if (model == null) {
            onException(null);
        }

        status = Status.WAITING_FOR_SIZE;

        if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
            onSizeReady(overrideWidth, overrideHeight);
        } else {
            target.getSize(this);
        }

        if (!isComplete() && !isPaused() && !isCancelled()) {
            target.onLoadStarted(getPlaceholderDrawable());
        }
    }

    private Drawable getPlaceholderDrawable() {
        if (placeholderDrawable == null && placeholderResourceId > 0) {
            placeholderDrawable = context.getResources().getDrawable(placeholderResourceId);
        }
        return placeholderDrawable;
    }


    @Override
    public void recycle() {
        loadProvider = null;
        model = null;
        context = null;
        target = null;
        placeholderDrawable = null;
        errorDrawable = null;
        requestListener = null;
        loadedFromMemoryCache = false;
        REQUEST_POOL.offer(this);
    }
    private static final String TAG = "GenericRequest";
    @Override
    public void onResourceReady(Resource<?> resource) {
        TranscodeType transcodeType = (TranscodeType) resource.get();
        this.resource = resource;
        GlideBitmapDrawable drawable = (GlideBitmapDrawable) transcodeType;
        Bitmap bitmap = drawable.getBitmap();

        Log.v(TAG, "onResourceReady  " + transcodeType+"  bitmap "+bitmap);
        if (requestListener == null || !requestListener.onResourceReady(transcodeType, model,
                target, true, true)) {
            target.onResourceReady(transcodeType);
        }
    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "onException() returned: " + e.getMessage());
    }

    @Override
    public void onSizeReady(int width, int height) {
        if (status != Status.WAITING_FOR_SIZE) {
            return;
        }
        status = Status.RUNNING;
        width = Math.round(sizeMultiplier * width);
        height = Math.round(sizeMultiplier * height);
        ModelLoader<ModelType, DataType> modelLoader = loadProvider.getModelLoader();
        //在这里将url传进去
        DataFetcher<DataType> dataFetcher = modelLoader.getResourceFetcher(model, width, height);
        if (dataFetcher == null) {
            onException(new Exception("NO DATA fetcher"));
        }

        ResourceTranscoder<ResourceType, TranscodeType> transcoder = loadProvider.getTranscoder();
        loadStatus = engine.load(signature, width, height, dataFetcher, loadProvider, transcoder, priority,
                isMemoryCacheable, diskCacheStrategy, this);
    }

    void cancel() {
        status = Status.CANCELLED;
        if (loadStatus != null) {
            loadStatus.cancel();
            loadStatus = null;
        }
    }

    @Override
    public void pause() {
        clear();
        status = Status.PAUSED;
    }

    @Override
    public void clear() {
        Util.assertMainThread();
        if (status == Status.CLEARED) {
            return;
        }
        cancel();
        // Resource must be released before canNotifyStatusChanged is called.
        if (resource != null) {
            releaseResource(resource);
        }

        target.onLoadCleared(getPlaceholderDrawable());

        // Must be after cancel().
        status = Status.CLEARED;
    }

    private void releaseResource(Resource resource) {
        engine.release(resource);
        this.resource = null;
    }

    @Override
    public boolean isPaused() {
        return status == Status.PAUSED;
    }

    @Override
    public boolean isRunning() {
        return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE;
    }

    @Override
    public boolean isComplete() {
        return status == Status.COMPLETE;
    }

    @Override
    public boolean isResourceSet() {
        return isComplete();
    }

    @Override
    public boolean isCancelled() {
        return status == Status.CANCELLED || status == Status.CLEARED;
    }

    @Override
    public boolean isFailed() {
        return status == Status.FAILED;
    }

}
