package com.lxw.glide.load.engine.cache;

import android.content.ComponentCallbacks2;

import com.lxw.glide.load.Key;
import com.lxw.glide.load.engine.Resource;
import com.lxw.glide.util.LruCache;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/03
 *     desc   :
 * </pre>
 */
public class LruResourceCache extends LruCache<Key, Resource<?>> implements MemoryCache {
    private ResourceRemovedListener listener;

    public LruResourceCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public void setResourceRemovedListener(ResourceRemovedListener listener) {
        this.listener = listener;
    }

    /**
     * 针对当前的 level ,进行内存回收
     *
     * @param level
     */
    @Override
    public void trimMomory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            clearMemory();
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            trimToSize(getCurrentSize() / 2);
        }
    }

    @Override
    protected void onItemEvicted(Key key, Resource<?> resource) {
        if (listener != null) {
            listener.onResourceRemoved(resource);
        }
    }

    @Override
    protected int getItemSize(Resource<?> resource) {
        return resource.getSize();
    }

}
