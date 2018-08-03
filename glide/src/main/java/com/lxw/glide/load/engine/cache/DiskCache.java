package com.lxw.glide.load.engine.cache;

import com.lxw.glide.load.Key;

import java.io.File;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public interface DiskCache {

    interface Factory {
        int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
        String DEFAULT_DISK_CACHE_DIR = "image_manage_disk_cache";

        DiskCache build();
    }

    interface Writer {
        boolean write(File file);
    }

    File get(Key key);

    void put(Key key, Writer writer);

    void delete(Key key);

    void clear();
}
