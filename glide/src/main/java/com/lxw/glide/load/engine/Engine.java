package com.lxw.glide.load.engine;

import android.graphics.Bitmap;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.lxw.glide.Priority;
import com.lxw.glide.load.Key;
import com.lxw.glide.load.data.DataFetcher;
import com.lxw.glide.load.engine.cache.DiskCache;
import com.lxw.glide.load.engine.cache.EngineJob;
import com.lxw.glide.load.engine.cache.MemoryCache;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;
import com.lxw.glide.provider.DataLoadProvider;
import com.lxw.glide.request.ResourceCallback;
import com.lxw.glide.util.LogTime;
import com.lxw.glide.util.Util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class Engine implements EngineJobListener, EngineResource.ResourceListener {
    private static final String TAG = "Engine";
    private MemoryCache cache;
    private ExecutorService diskCacheService;
    //从
    private ExecutorService sourceService;
    //    private Map<Key, Resource> activities;
    private final EngineKeyFactory keyFactory;
    private final Map<Key, EngineJob> jobs;
    private final EngineJobFactory engineJobFactory;
    private final LazyDiskCacheProvider diskCacheProvider;
    private final Map<Key, ResourceWeakReference> activeResources;
    private ResourceRecycler resourceRecycler;
    private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;


    public Engine(MemoryCache memoryCache, DiskCache.Factory diskCacheFactory,
                  ExecutorService diskCacheService, ExecutorService sourceService) {
        this.cache = memoryCache;
        this.diskCacheProvider = new LazyDiskCacheProvider(diskCacheFactory);
        this.diskCacheService = diskCacheService;
        this.sourceService = sourceService;

        this.keyFactory = new EngineKeyFactory();
        activeResources = new HashMap<>();
        this.jobs = new HashMap<>();
        engineJobFactory = new EngineJobFactory(diskCacheService, sourceService, this);
        resourceRecycler = new ResourceRecycler();
    }


    public <DataType, ResourceType, TranscodeType> Engine.LoadStatus load(Key signature, int width, int height,
                                                                          DataFetcher<DataType> dataFetcher,
                                                                          DataLoadProvider<DataType, ResourceType> loadProvider,
                                                                          ResourceTranscoder<ResourceType, TranscodeType> transcoder,
                                                                          Priority priority, boolean isMemoryCacheable,
                                                                          DiskCacheStrategy diskCacheStrategy,
                                                                          ResourceCallback cb) {
        long startTime = LogTime.getLogTime();
        final String id = dataFetcher.getId();
        EngineKey key = keyFactory.build(id, signature, width, height, loadProvider.getCacheDecoder(),
                loadProvider.getSourceDecoder(), loadProvider.getEncoder(),
                transcoder, loadProvider.getSourceEncoder());
        EngineResource<?> cached = loadFromCache(key, isMemoryCacheable);
        if (cached != null) {
            cb.onResourceReady(cached);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                logWithTimeAndKey("Loaded resource from cache", startTime, key);
            }
            return null;
        }
        EngineResource<?> active = loadFromActiveResources(key, isMemoryCacheable);
        if (null != active) {
            cb.onResourceReady(active);
//            if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Loaded resource from active resources", startTime, key);
            Log.v(TAG, "active" + active);
//            }
            return null;
        }

        EngineJob current = jobs.get(key);
        if (current != null) {
            current.addCallback(cb);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                logWithTimeAndKey("Added to existing load", startTime, key);
            }
            return new LoadStatus(current, cb);
        }
        EngineJob engineJob = engineJobFactory.build(key, isMemoryCacheable);
        DecodeJob<DataType, ResourceType, TranscodeType> decodeJob =
                new DecodeJob<>(
                        key, width, height,
                        dataFetcher, loadProvider,
                        transcoder, diskCacheProvider, diskCacheStrategy,
                        priority
                );
        jobs.put(key, engineJob);
        engineJob.addCallback(cb);
        EngineRunnable runnable = new EngineRunnable(priority, engineJob, decodeJob);
        engineJob.start(runnable);
        // if (Log.isLoggable(TAG, Log.VERBOSE)) {
        logWithTimeAndKey("Started new load", startTime, key);
        // }
        return new LoadStatus(engineJob, cb);
    }

    private static void logWithTimeAndKey(String log, long startTime, Key key) {
        Log.v(TAG, log + " in " + LogTime.getElapsedMillis(startTime) + "ms, key: " + key);
    }

    private EngineResource<?> loadFromActiveResources(EngineKey key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        EngineResource<?> active = null;
        ResourceWeakReference activeRef = activeResources.get(key);
        if (activeRef != null) {
            active = activeRef.get();
            if (active != null) {
                active.acquire();
            } else {
                activeResources.remove(key);
            }
        }
        return active;

    }

    private EngineResource<?> loadFromCache(EngineKey key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        EngineResource<?> cached = getEngineResourceFromCache(key);
        if (cached != null) {
            cached.acquire();
            activeResources.put(key, new ResourceWeakReference(key, cached, getResourceReferenceQueue()));
        }
        return cached;
    }

    private EngineResource<?> getEngineResourceFromCache(EngineKey key) {
        Resource<?> cached = cache.remove(key);
        final EngineResource result;
        if (cached == null) {
            result = null;
        } else if (cached instanceof EngineResource) {
            result = (EngineResource) cached;
        } else {
            result = new EngineResource(cached, true);
        }
        return result;
    }

    @Override
    public  EngineResource<?> onEngineJobComplete(Key key, EngineResource<?> resource) {
        //每一个加载成功的资源都需要进行resourceListener的监听
//        Log.d(TAG, "onEngineJobComplete() returned: " + resource.get());
        Log.d(TAG, "onEngineJobComplete() returned: " + resource);
        if (resource != null) {
            GlideBitmapDrawable drawable = (GlideBitmapDrawable) resource.get();
            Bitmap bitmap = drawable.getBitmap();
            Log.d(TAG, "onEngineJobComplete() returned: " + bitmap);
            resource.setResourceListener(key, this);
            if (resource.isCacheable()) {
                activeResources.put(key, new ResourceWeakReference(key, resource, getResourceReferenceQueue()));
                return resource;
            }
        }

        //表示 该网络请求任务已完成
        jobs.remove(key);
        return null;
    }

    @Override
    public void onEngineJobCancelled(EngineJob engineJob, Key key) {
        if (engineJob.getKey().equals(key)) {
            jobs.remove(key);
        }

    }

    @Override
    public EngineResource<?> hah(EngineResource<?> resource) {
        return resource;
    }

    @Override
    public void onResourceReleased(Key key, EngineResource<?> resource) {
        //引用计数为0回调该函数
        activeResources.remove(key);
        if (resource.isCacheable()) {
            cache.put(key, resource);
        } else {
            resourceRecycler.recycle(resource);
        }
    }

    public static class LoadStatus {
        private final EngineJob engineJob;
        private final ResourceCallback cb;

        public LoadStatus(EngineJob engineJob, ResourceCallback cb) {
            this.engineJob = engineJob;
            this.cb = cb;
        }

        public void cancel() {
//            engineJob.removeCallback(cb);
        }
    }

    private ReferenceQueue<EngineResource<?>> getResourceReferenceQueue() {
        if (resourceReferenceQueue == null) {
            resourceReferenceQueue = new ReferenceQueue<>();
            MessageQueue queue = Looper.myQueue();
            queue.addIdleHandler(new RefQueueIdleHandler(activeResources, resourceReferenceQueue));
        }
        return resourceReferenceQueue;
    }

    private static class RefQueueIdleHandler implements MessageQueue.IdleHandler {
        private final Map<Key, ResourceWeakReference> activeResources;
        private final ReferenceQueue<EngineResource<?>> queue;

        public RefQueueIdleHandler(Map<Key, ResourceWeakReference> activeResources,
                                   ReferenceQueue<EngineResource<?>> queue) {
            this.activeResources = activeResources;
            this.queue = queue;
        }

        @Override
        public boolean queueIdle() {
            ResourceWeakReference ref = (ResourceWeakReference) queue.poll();
            if (ref != null) {
                activeResources.remove(ref.key);
            }

            return true;
        }
    }

    private static class ResourceWeakReference extends WeakReference<EngineResource<?>> {
        private final Key key;

        public ResourceWeakReference(Key key, EngineResource<?> referent, ReferenceQueue<? super EngineResource<?>> q) {
            super(referent, q);
            this.key = key;
        }
    }

    static class EngineJobFactory {
        private final ExecutorService diskCacheService;
        private final ExecutorService sourceService;
        private final EngineJobListener listener;

        public EngineJobFactory(ExecutorService diskCacheService, ExecutorService sourceService,
                                EngineJobListener listener) {
            this.diskCacheService = diskCacheService;
            this.sourceService = sourceService;
            this.listener = listener;
        }

        public EngineJob build(Key key, boolean isMemoryCacheable) {
            return new EngineJob(key, diskCacheService, sourceService, isMemoryCacheable, listener);
        }
    }

    private static class LazyDiskCacheProvider implements DecodeJob.DiskCacheProvider {

        private final DiskCache.Factory factory;
        private volatile DiskCache diskCache;

        public LazyDiskCacheProvider(DiskCache.Factory factory) {
            this.factory = factory;
        }

        @Override
        public DiskCache getDiskCache() {
            if (diskCache == null) {
                synchronized (this) {
                    if (diskCache == null) {
                        diskCache = factory.build();
                    }
                }
            }
            return diskCache;
        }
    }

    public void release(Resource resource) {
        Util.assertMainThread();
        if (resource instanceof EngineResource) {
            ((EngineResource) resource).release();
        } else {
            throw new IllegalArgumentException("Cannot release anything but an EngineResource");
        }
    }
}
