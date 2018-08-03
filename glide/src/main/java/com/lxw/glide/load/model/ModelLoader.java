package com.lxw.glide.load.model;

import com.lxw.glide.load.data.DataFetcher;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface ModelLoader<T, Y> {
    // DataFetcher 实际获取数据的类
    DataFetcher<Y> getResourceFetcher(T model,int width,int height);
}
