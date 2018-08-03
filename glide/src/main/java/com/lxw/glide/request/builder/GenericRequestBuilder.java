package com.lxw.glide.request.builder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.lxw.glide.Glide;
import com.lxw.glide.Priority;
import com.lxw.glide.load.Key;
import com.lxw.glide.load.engine.DiskCacheStrategy;
import com.lxw.glide.manager.Lifecycle;
import com.lxw.glide.manager.RequestTracker;
import com.lxw.glide.provider.LoadProvider;
import com.lxw.glide.request.GenericRequest;
import com.lxw.glide.request.Request;
import com.lxw.glide.request.RequestListener;
import com.lxw.glide.request.target.Target;
import com.lxw.glide.signature.EmptySignature;
import com.lxw.glide.util.Util;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> {
    protected final Class<ModelType> modelClass;
    protected final Context context;
    protected final Glide glide;
    protected final Class<TranscodeType> transcodeTypeClass;
    protected final RequestTracker requestTracker;
    protected final Lifecycle lifecycle;
    private ModelType model;
    private boolean isModelSet;
    private Priority priority = null;
    private int placeholderId;
    private int errorId;
    private Drawable placeholderDrawable;
    private Drawable errorDrawable;
    private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.RESULT;
    private RequestListener<? super ModelType, TranscodeType> requestListener;
    private Key signature = EmptySignature.obtain();
    private boolean isCacheable = true;
    private int overrideHeight = -1;
    private int overrideWidth = -1;
    private LoadProvider<ModelType, DataType, ResourceType, TranscodeType> loadProvider;

    public GenericRequestBuilder(Context context, Class<ModelType> modelClass,
                                 Class<TranscodeType> transcodeTypeClass, Glide glide,
                                 LoadProvider<ModelType, DataType, ResourceType, TranscodeType> loadProvider,
                                 RequestTracker requestTracker,
                                 Lifecycle lifecycle) {

        this.context = context;
        this.modelClass = modelClass;
        this.glide = glide;
        this.transcodeTypeClass = transcodeTypeClass;
        this.requestTracker = requestTracker;
        this.lifecycle = lifecycle;
        this.loadProvider = loadProvider;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> load(ModelType model) {
        this.model = model;
        isModelSet = true;
        return this;
    }

    public Target<TranscodeType> into(ImageView view) {
        Util.assertMainThread();
        if (view == null) {
            throw new IllegalArgumentException("You must pass in a non null View");
        }
        Target target = glide.buildImageViewTarget(view, transcodeTypeClass);
        return into(target);
    }

    private Target<TranscodeType> into(Target target) {
        Util.assertMainThread();
        if (target == null) {
            throw new IllegalArgumentException("You must pass in a non null Target");
        }
        if (!isModelSet) {
            throw new IllegalArgumentException("You must first set a model (try #load())");
        }
        Request previous = target.getRequest();
        if (previous != null) {
            previous.clear();
            requestTracker.removeRequest(previous);
            previous.recycle();
        }

        Request request = buildRequest(target);
        target.setRequest(request);
        lifecycle.addListener(target);
        requestTracker.runRequest(request);
        return target;
    }

    private Request buildRequest(Target target) {
        if (priority == null) {
            priority = Priority.NORMAL;
        }
        return obtainRequest(target, priority);
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> diskCacheStrategy(
            DiskCacheStrategy strategy) {
        this.diskCacheStrategy = strategy;

        return this;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> error(int errorId) {
        this.errorId = errorId;
        return this;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> error(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        return this;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> skipMemoryCache(boolean skip) {
        this.isCacheable = !skip;

        return this;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> placeholder(int placeholderId) {
        this.placeholderId = placeholderId;
        return this;
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> placeholder(Drawable placeholderDrawable) {
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    public GenericRequestBuilder setRequestListener(RequestListener<? super ModelType, TranscodeType> listener) {
        this.requestListener = listener;
        return this;
    }

    private Request obtainRequest(Target target, Priority priority) {

        return GenericRequest.obtain(
                loadProvider,
                model,
                signature,
                context,
                priority,
                target,
                1.0f,
                placeholderDrawable,
                placeholderId,
                errorDrawable,
                errorId,
                requestListener,
                glide.getEngine(),
                isCacheable,
                overrideWidth,
                overrideHeight,
                diskCacheStrategy
        );
    }

    public GenericRequestBuilder<ModelType, DataType, ResourceType, TranscodeType> override(int width, int height) {
        overrideHeight = height;
        overrideWidth = width;
        return this;
    }
}
