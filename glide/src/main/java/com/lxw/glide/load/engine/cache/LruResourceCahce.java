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
public class LruResourceCahce implements MemoryCache {
    public LruResourceCahce(int memoryCacheSize) {
    }

    @Override
    public Resource<?> put(Key key, Resource<?> resource) {
        return null;
    }

    @Override
    public Resource<?> remove(Key key) {
        return null;
    }

    @Override
    public void setResourceRemovedListener(ResourceRemovedListener listener) {

    }

    @Override
    public void clearMemory() {

    }

    @Override
    public void trimMomory(int level) {

    }

    @Override
    public int getCurrentSize() {
        return 0;
    }

    @Override
    public int getMaxSize() {
        return 0;
    }

    @Override
    public void setSizeMultiplier(float multiplier) {

    }
}
