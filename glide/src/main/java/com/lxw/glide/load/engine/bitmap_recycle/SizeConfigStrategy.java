package com.lxw.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;

import com.lxw.glide.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/03
 *     desc   :
 * </pre>
 */
public class SizeConfigStrategy implements LruPoolStrategy {
    private static final int MAX_SIZE_MULTIPLE = 8;

    private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS = new Bitmap.Config[]{
            Bitmap.Config.ARGB_8888,
            // The value returned by Bitmaps with the hidden Bitmap config.
            null,
    };
    // We probably could allow ARGB_4444 and RGB_565 to decode into each other, but ARGB_4444 is deprecated and we'd
    // rather be safe.
    private static final Bitmap.Config[] RGB_565_IN_CONFIGS = new Bitmap.Config[]{
            Bitmap.Config.RGB_565
    };
    private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS = new Bitmap.Config[]{
            Bitmap.Config.ARGB_4444
    };
    private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS = new Bitmap.Config[]{
            Bitmap.Config.ALPHA_8
    };

    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<Key, Bitmap>();
    private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes =
            new HashMap<Bitmap.Config, NavigableMap<Integer, Integer>>();

    @Override
    public void put(Bitmap bitmap) {
        int size = Util.getBitmapByteSize(bitmap);
        Key key = new Key(size, bitmap.getConfig());
        groupedMap.put(key, bitmap);

    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        int size = Util.getBitmapByteSize(width, height, config);
        Key key = new Key(size, config);
        return groupedMap.get(key);

    }

    @Override
    public Bitmap removeLast() {
        return groupedMap.removeLast();
    }

    @Override
    public String logBitmap(Bitmap bitmap) {
        return getBitmapString(Util.getBitmapByteSize(bitmap), bitmap.getConfig());
    }

    @Override
    public String logBitmap(int width, int height, Bitmap.Config config) {
        return getBitmapString(Util.getBitmapByteSize(width, height, config), config);
    }

    @Override
    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    static final class Key {


        private int size;
        private Bitmap.Config config;

        // Visible for testing.
        Key(int size, Bitmap.Config config) {
            init(size, config);
        }

        public void init(int size, Bitmap.Config config) {
            this.size = size;
            this.config = config;
        }


        @Override
        public String toString() {
            return getBitmapString(size, config);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                return size == other.size && (config == null ? other.config == null : config.equals(other.config));
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }
    }

    private static String getBitmapString(int size, Bitmap.Config config) {
        return "[" + size + "](" + config + ")";
    }
}
