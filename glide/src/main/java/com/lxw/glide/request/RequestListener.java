package com.lxw.glide.request;

import com.lxw.glide.request.target.Target;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface RequestListener<T, R> {

    boolean onException(Exception e, T model, Target<R> target, boolean isFirstResource);

    boolean onResourceReady(R resource, T model, Target<R> target, boolean isFromMemoryCache, boolean isFirstResource);
}
