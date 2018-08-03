package com.lxw.glide.module;

import android.content.Context;

import com.lxw.glide.Glide;
import com.lxw.glide.GlideBuilder;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public interface GlideModule {

    void applyOptions(Context context, GlideBuilder builder);

    void registerComponents(Context context, Glide glide);
}
