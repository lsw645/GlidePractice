package com.lxw.glide.load.engine;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public enum DiskCacheStrategy {
    //
    ALL(true, true),
    //
    NONE(false, false),
    //
    SOURCE(true, false),
    //
    RESULT(false, true);

    private final boolean cacheSource;
    private final boolean cacheResult;

    DiskCacheStrategy(boolean cacheSource, boolean cacheResult) {
        this.cacheSource = cacheSource;
        this.cacheResult = cacheResult;
    }

    public boolean cacheSource() {
        return cacheSource;
    }

    public boolean cacheResult() {
        return cacheResult;
    }
}
