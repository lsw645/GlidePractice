package com.lxw.glide.request.target;

import android.graphics.drawable.Drawable;

import com.lxw.glide.manager.LifecycleListener;
import com.lxw.glide.request.Request;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface Target<R> extends LifecycleListener {

    int SIZE_ORIGINAL = Integer.MIN_VALUE;

    void onLoadStarted(Drawable placeholder);

    void onLoadFailed(Exception e, Drawable errorDrawable);

    void onResourceReady(R resource);

    void onLoadCleared(Drawable placeholder);

    void getSize(SizeReadyCallback cb);

    void setRequest(Request request);

    Request getRequest();
}
