package com.lxw.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class LruBitmaPool implements BitmapPool {
    private static final String TAG = "LruBitmaPool";
    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
    private final LruPoolStrategy strategy;
    private final Set<Bitmap.Config> allowedConfigs;
    private final int initalMaxSize;

    private int maxSize;
    private int currentSize;
    private int hits;
    private int misses;
    private int puts;
    private int evictions;

    public LruBitmaPool(int maxSize) {
        this(maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
    }

    public LruBitmaPool(int maxSize, LruPoolStrategy strategy, Set<Bitmap.Config> allowedConfigs) {
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.initalMaxSize = maxSize;
        this.maxSize = maxSize;
    }

    @Override
    public boolean put(Bitmap bitmap) {
        return false;
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        Bitmap result = getDirty(width, height, config);
        if (result != null) {
            result.eraseColor(Color.TRANSPARENT);
        }
        return result;
    }

    @Override
    public void clearMemory() {
        trimToSize(0);
    }

    @Override
    public Bitmap getDirty(int width, int height, Bitmap.Config config) {
        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        final Bitmap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            misses++;
        } else {
            hits++;
            currentSize -= strategy.getSize(result);
            result.setHasAlpha(true);
        }

        return result;
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            trimToSize(maxSize / 2);
        }
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void setSizeMultiplier(float sizeMultiplier) {
        maxSize = Math.round(initalMaxSize * sizeMultiplier);
        evit();
    }

    private void evit() {
        trimToSize(maxSize);
    }


    private synchronized void trimToSize(int size) {
        while (currentSize > size) {
            final Bitmap removed = strategy.removeLast();
            // TODO: This shouldn't ever happen, see #331.
            if (removed == null) {
                currentSize = 0;
                return;
            }

            currentSize -= strategy.getSize(removed);
            removed.recycle();
            evictions++;
        }
    }

    private static LruPoolStrategy getDefaultStrategy() {
        return new SizeConfigStrategy();
    }

    private static Set<Bitmap.Config> getDefaultAllowedConfigs() {
        Set<Bitmap.Config> configs = new HashSet<Bitmap.Config>();
        configs.addAll(Arrays.asList(Bitmap.Config.values()));
        if (Build.VERSION.SDK_INT >= 19) {
            configs.add(null);
        }
        return Collections.unmodifiableSet(configs);
    }
}
