package com.lxw.glide.load.engine.cache;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lxw.glide.load.Key;
import com.lxw.glide.load.engine.EngineJobListener;
import com.lxw.glide.load.engine.EngineResource;
import com.lxw.glide.load.engine.EngineRunnable;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.request.ResourceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class EngineJob implements EngineRunnable.EngineRunnableManager {
    private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback());

    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;

    private EngineRunnable engineRunnable;
    private final List<ResourceCallback> cbs = new ArrayList<ResourceCallback>();
    private final EngineResourceFactory engineResourceFactory;
    private final EngineJobListener listener;
    private final Key key;
    //从磁盘中获取数据时使用该线程池
    private final ExecutorService diskCacheService;
    //获取原始数据时使用该线程池
    private final ExecutorService sourceService;
    private final boolean isCacheable;
    private Resource<?> resource;
    private EngineResource<?> engineResource;
    private boolean hasResource;
    private boolean isCancelled;
    private boolean hasException;
    private Exception exception;
    private Future future;

    public EngineJob(Key key, ExecutorService diskCacheService,
                     ExecutorService sourceService, boolean isMemoryCacheable, EngineJobListener listener) {
        this(key, diskCacheService, sourceService, isMemoryCacheable, listener, DEFAULT_FACTORY);
    }

    public EngineJob(Key key, ExecutorService diskCacheService,
                     ExecutorService sourceService,
                     boolean isMemoryCacheable,
                     EngineJobListener listener,
                     EngineResourceFactory factory) {
        this.key = key;
        this.diskCacheService = diskCacheService;
        this.sourceService = sourceService;
        this.isCacheable = isMemoryCacheable;
        this.listener = listener;
        this.engineResourceFactory = factory;
    }

    public void addCallback(ResourceCallback cb) {
        cbs.add(cb);
    }

    @Override
    public void submitForSource(EngineRunnable runnable) {
        future = sourceService.submit(runnable);
    }

    public Key getKey() {
        return key;
    }
    private static final String TAG = "EngineJob";
    @Override
    public void onResourceReady(Resource<?> resource) {
        Log.d(TAG, "第一次 onResourceReady() returned: " + resource);
        this.resource = resource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_COMPLETE, this).sendToTarget();
    }

    @Override
    public void onException(final Exception e) {
        this.exception = e;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_EXCEPTION, this).sendToTarget();
    }

    public void start(EngineRunnable runnable) {
        this.engineRunnable = runnable;
        future = diskCacheService.submit(runnable);
    }

    // Visible for testing.
    static class EngineResourceFactory {
        public <R> EngineResource<R> build(Resource<R> resource, boolean isMemoryCacheable) {
            return new EngineResource<R>(resource, isMemoryCacheable);
        }
    }

    private static class MainThreadCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {
            if (MSG_COMPLETE == message.what || MSG_EXCEPTION == message.what) {
                EngineJob job = (EngineJob) message.obj;
                //MSG_COMPLETE
                if (MSG_COMPLETE == message.what) {
                    //处理正常的回调
                    job.handleResultOnMainThread();
                } else {
                    job.handleExceptionOnMainThread();
                }
                return true;
            }

            return false;
        }
    }

    private void handleResultOnMainThread() {
        if (isCancelled) {
            resource.recycle();
            return;
        } else if (cbs.isEmpty()) {
            throw new IllegalStateException("Received a resource without any callbacks to notify");
        }
        Log.d(TAG, "handleResultOnMainThread() returned: " + resource);
        engineResource = engineResourceFactory.build(resource, isCacheable);
        hasResource = true;

        // Hold on to resource for duration of request so we don't recycle it in the middle of notifying if it
        // synchronously released by one of the callbacks.
        engineResource.acquire();
       listener.onEngineJobComplete(key, engineResource);

        for (ResourceCallback cb : cbs) {
            engineResource.acquire();
            cb.onResourceReady(resource);
        }
        // Our request is complete, so we can release the resource.
        engineResource.release();
    }

    private void handleExceptionOnMainThread() {
        if (isCancelled) {
            return;
        } else if (cbs.isEmpty()) {
            throw new IllegalStateException("Received an exception without any callbacks to notify");
        }
        hasException = true;

        listener.onEngineJobComplete(key, null);

        for (ResourceCallback cb : cbs) {
            cb.onException(exception);
        }
    }

    void cancel() {
        if (hasException || hasResource || isCancelled) {
            return;
        }
        engineRunnable.cancel();
        Future currentFuture = future;
        if (currentFuture != null) {
            currentFuture.cancel(true);
        }
        isCancelled = true;
        listener.onEngineJobCancelled(this, key);
    }

}
