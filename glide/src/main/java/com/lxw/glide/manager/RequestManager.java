package com.lxw.glide.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.request.builder.DrawableTypeRequest;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class RequestManager implements LifecycleListener {
    private final Lifecycle lifecycle;
    private final Context context;
    private final Glide glide;
    private RequestTracker requestTracker;

    public RequestManager(Context context, final Lifecycle lifecycle) {
        this.context = context.getApplicationContext();
        this.lifecycle = lifecycle;
        this.glide = Glide.get(context);
        requestTracker = new RequestTracker();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                lifecycle.addListener(RequestManager.this);
            }
        });
    }

    public DrawableTypeRequest<String> load(String url) {
        return (DrawableTypeRequest<String>) loadGeneric(String.class).load(url);
    }

    ;

    private <T> DrawableTypeRequest<T> loadGeneric(Class<T> modelClass) {
        ModelLoader<T, InputStream> streamLoader = Glide.buildModelLoader(modelClass, InputStream.class, context);
        ModelLoader<T, ParcelFileDescriptor> fileDescriptorModelLoader = Glide.buildModelLoader(modelClass, ParcelFileDescriptor.class, context);
        //TODO 两个ModelLoader 的实现
        return new DrawableTypeRequest<T>(modelClass, streamLoader, fileDescriptorModelLoader,
                context, glide, requestTracker, lifecycle);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
