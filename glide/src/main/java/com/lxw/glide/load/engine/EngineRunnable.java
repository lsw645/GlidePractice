package com.lxw.glide.load.engine;

import android.util.Log;

import com.lxw.glide.Priority;
import com.lxw.glide.load.engine.executor.Prioritized;
import com.lxw.glide.request.ResourceCallback;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class EngineRunnable implements Runnable, Prioritized {
    private static final String TAG = "EngineRunnable";
    private final Priority priority;
    private final EngineRunnableManager manager;
    private final DecodeJob decodeJob;
    private Stage stage;
    private volatile boolean isCancelled;

    public EngineRunnable(Priority priority, EngineRunnableManager manager, DecodeJob decodeJob) {
        this.priority = priority;
        this.manager = manager;
        this.stage = Stage.CACHE;
        this.decodeJob = decodeJob;
    }

    @Override
    public void run() {
        if (isCancelled) {
            return;
        }

        Exception exception = null;
        Resource<?> resource = null;
        try {
            resource = decode();
        } catch (Exception e) {
            exception = e;
        }

        if (isCancelled) {
            if (resource != null) {
                resource.recycle();
            }
            return;
        }

        if (resource == null) {
            onLoadFailed(exception);
        } else {
            onLoadComplete(resource);
        }
    }

    private void onLoadComplete(Resource<?> resource) {
        manager.onResourceReady(resource);
    }

    private void onLoadFailed(Exception e) {
        if (isDecodingFromCache()) {
            stage = Stage.SOURCE;
            //首先从磁盘缓存试图获取数据，如果获取失败，再进行其他方式的获取
            manager.submitForSource(this);
        } else {
            manager.onException(e);
        }
    }

    private boolean isDecodingFromCache() {
        return this.stage == Stage.CACHE;
    }

    private Resource<?> decode() throws Exception {
        if (isDecodingFromCache()) {
            return decodeFromCache();
        } else {
            return decodeFromSource();
        }
    }

    private Resource<?> decodeFromSource() throws Exception {
        return decodeJob.decodeFromSource();
    }

    private Resource<?> decodeFromCache() throws Exception {
        Resource<?> result = null;
        //DiskCacheStrategy.RESULT
        try {
            result = decodeJob.decodeResultFromCache();
        } catch (Exception e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Exception decoding result from cache: " + e);
            }
        }
        //DiskCacheStrategy.SOURCE这两个参数的区别，
        if (result == null) {
            result = decodeJob.decodeSourceFromCache();
        }
        return result;
    }


    public void cancel() {
        isCancelled = true;
    }

    @Override
    public int getPriority() {
        return priority.ordinal();
    }

    private enum Stage {
        CACHE,
        SOURCE
    }

    public interface EngineRunnableManager extends ResourceCallback {
        void submitForSource(EngineRunnable runnable);
    }
}
