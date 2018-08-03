package com.lxw.glide;

import android.content.Context;

import com.lxw.glide.load.engine.DecodeFormat;
import com.lxw.glide.load.engine.Engine;
import com.lxw.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lxw.glide.load.engine.bitmap_recycle.LruBitmaPool;
import com.lxw.glide.load.engine.cache.DiskCache;
import com.lxw.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.lxw.glide.load.engine.cache.LruResourceCahce;
import com.lxw.glide.load.engine.cache.MemoryCache;
import com.lxw.glide.load.engine.cache.MemorySizeCalculator;
import com.lxw.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;

import java.util.concurrent.ExecutorService;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class GlideBuilder {

    private final Context context;
    private Engine engine;
    private MemoryCache memoryCache;
    private BitmapPool bitmapPool;
    private ExecutorService sourceService;
    private ExecutorService diskCacheService;
    private DecodeFormat decodeFormat;
    private DiskCache.Factory diskCacheFactory;


    public GlideBuilder(Context context) {
        this.context = context.getApplicationContext();
    }


    public GlideBuilder setEngine(Engine engine) {
        this.engine = engine;
        return this;
    }

    public GlideBuilder setMemoryCache(MemoryCache memoryCache) {
        this.memoryCache = memoryCache;
        return this;
    }

    public GlideBuilder setBitmapPool(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
        return this;
    }

    public GlideBuilder setSourceService(ExecutorService sourceService) {
        this.sourceService = sourceService;
        return this;
    }

    public GlideBuilder setDiskCacheService(ExecutorService diskCacheService) {
        this.diskCacheService = diskCacheService;
        return this;
    }

    public GlideBuilder setDecodeFormat(DecodeFormat decodeFormat) {
        this.decodeFormat = decodeFormat;
        return this;
    }

    public GlideBuilder setDiskCache(final DiskCache diskCache) {
        return setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                return diskCache;
            }
        });
    }

    public GlideBuilder setDiskCache(DiskCache.Factory diskCacheFactory) {
        this.diskCacheFactory = diskCacheFactory;
        return this;
    }

    public Glide createGlide() {
        if (sourceService == null) {
            final int cores = Math.max(1, Runtime.getRuntime().availableProcessors());
            sourceService = new FifoPriorityThreadPoolExecutor(cores);
        }

        if (diskCacheService == null) {
            diskCacheService = new FifoPriorityThreadPoolExecutor(1);
        }

        MemorySizeCalculator calculator = new MemorySizeCalculator(context);

        if (bitmapPool == null) {
            int bitmapPoolSize = calculator.getBitmapPoolSize();
            bitmapPool = new LruBitmaPool(bitmapPoolSize);
        }

        if(memoryCache == null){
            memoryCache  = new LruResourceCahce(calculator.getMemoryCacheSize());
        }
        if(diskCacheFactory == null){
            diskCacheFactory  = new InternalCacheDiskCacheFactory(context);
        }
        if (engine == null) {
            engine = new Engine(memoryCache, diskCacheFactory, diskCacheService, sourceService);
        }

        if (decodeFormat == null) {
            decodeFormat = DecodeFormat.DEFAULT;
        }

        return new Glide(engine,memoryCache,context,bitmapPool,decodeFormat);
    }


}
