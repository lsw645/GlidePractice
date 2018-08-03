package com.lxw.glide.request;

import com.lxw.glide.load.engine.Resource;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface ResourceCallback {

    void onResourceReady(Resource<?> resource);

    void onException(Exception e);
}
