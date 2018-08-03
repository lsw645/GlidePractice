package com.lxw.glide.request;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface Request {

    void begin();

    void pause();

    void clear();

    boolean isPaused();

    boolean isRunning();

    boolean isComplete();

    boolean isResourceSet();

    boolean isCancelled();

    boolean isFailed();

    void recycle();
}
