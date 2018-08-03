package com.lxw.glide.load.engine;

import android.os.Looper;

import com.lxw.glide.load.Key;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class EngineResource<Z> implements Resource<Z> {
    private final Resource<Z> resource;
    private final boolean isCacheable;
    private Key key;
    private int acquired;
    private boolean isRecycled;
    private ResourceListener listener;

    interface ResourceListener {
        void onResourceReleased(Key key, EngineResource<?> resource);
    }

    public EngineResource(Resource<Z> resource, boolean isCacheable) {
        this.resource = resource;
        this.isCacheable = isCacheable;
    }

    void setResourceListener(Key key, ResourceListener listener) {
        this.key = key;
        this.listener = listener;
    }

    boolean isCacheable() {
        return isCacheable;
    }

    @Override
    public Z get() {
        return resource.get();
    }

    @Override
    public int getSize() {
        return resource.getSize();
    }

    @Override
    public void recycle() {
        if (acquired > 0) {
            throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
        }
        if (isRecycled) {
            throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
        }
        isRecycled = true;
        resource.recycle();
    }

    public  void acquire() {
        if (isRecycled) {
            throw new IllegalStateException("Cannot acquire a recycled resource");
        }
        if (!Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new IllegalThreadStateException("Must call acquire on the main thread");
        }
        ++acquired;
    }

    public void release() {
        if (acquired <= 0) {
            throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
        }
        if (!Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new IllegalThreadStateException("Must call release on the main thread");
        }
        if (--acquired == 0) {
            listener.onResourceReleased(key, this);
        }
    }
}
