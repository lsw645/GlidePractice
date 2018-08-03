package com.lxw.glide.load.data;

import com.lxw.glide.Priority;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface DataFetcher<T> {

    T loadData(Priority priority) throws Exception;

    void cleanup();

    String getId();

    void cancel();
}
