package com.lxw.glide.load.model;

import android.content.Context;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/31
 *     desc   :
 * </pre>
 */
public interface ModelLoaderFactory<T, Y> {

    ModelLoader<T, Y> build(Context context, GenericLoaderFactory factories);

    void teardown();
}
