package com.lxw.glide.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/03
 *     desc   :
 * </pre>
 */
public class LruCache<K, V> {
    private final LinkedHashMap<K, V> cache =
            new LinkedHashMap<>(100, 0.75f, true);
    private int maxSize;
    private final int initialMaxSize;
    private int currentSize = 0;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        initialMaxSize = maxSize;
    }

    public V put(K k, V v) {
        int itemSize = getItemSize(v);
        if (itemSize > maxSize) {
            onItemEvicted(k,v);
            return null;
        }

        V old = cache.put(k, v);
        currentSize += itemSize;
        if (old != null) {
            currentSize -= getItemSize(old);
        }
        evict();
        return old;

    }


    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the sum of the sizes of all items in the cache.
     */
    public int getCurrentSize() {
        return currentSize;
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    /**
     * Returns the item in the cache for the given key or null if no such item exists.
     *
     * @param key The key to check.
     */
    public V get(K key) {
        return cache.get(key);
    }


    protected int getItemSize(V v) {
        return 1;
    }

    protected void onItemEvicted(K k, V v) {
        // optional override
    }

    protected void trimToSize(int size) {
        Map.Entry<K, V> last;
        while (currentSize > size) {
            last = cache.entrySet().iterator().next();
            final V toRemove = last.getValue();
            currentSize -= getItemSize(toRemove);
            final K key = last.getKey();
            cache.remove(key);
            onItemEvicted(key, toRemove);
        }
    }

    private void evict() {
        trimToSize(maxSize);
    }

    public V remove(K key) {
        final V value = cache.remove(key);
        if (value != null) {
            currentSize -= getItemSize(value);
        }
        return value;
    }

    public void clearMemory() {
        trimToSize(0);
    }

    public void setSizeMultiplier(float multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        maxSize = Math.round(initialMaxSize * multiplier);
        evict();
    }

}
