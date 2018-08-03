package com.lxw.glide.load.engine.cache;

import com.lxw.glide.load.Key;
import com.lxw.glide.load.engine.Resource;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public interface MemoryCache {

    interface ResourceRemovedListener {
        void onResourceRemoved(Resource<?> removed);
    }

    Resource<?> put(Key key, Resource<?> resource);

    Resource<?> remove(Key key);

    void setResourceRemovedListener(ResourceRemovedListener listener);

    void clearMemory();

    void trimMomory(int level);

    /**
     * Returns the sum of the sizes of all the contents of the cache in bytes.
     */
    int getCurrentSize();

    /**
     * Returns the current maximum size in bytes of the cache.
     */
    int getMaxSize();

    /**
     * Adjust the maximum size of the cache by multiplying the original size of the cache by the given multiplier.
     *
     * <p>
     *     If the size multiplier causes the size of the cache to be decreased, items will be evicted until the cache
     *     is smaller than the new size.
     * </p>
     *
     * @param multiplier A size multiplier >= 0.
     */
    void setSizeMultiplier(float multiplier);
}
