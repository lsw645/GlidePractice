package com.lxw.glide.load;

import java.io.OutputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :  负责写数据到 缓存中，一般是 file
 *
 * An interface for writing data to some persistent data store (i.e. a local File cache).
 *
 * @param <T> The type of the data that will be written.
 *
 * </pre>
 */
public interface Encoder<T> {

    boolean encode(T data, OutputStream os);

    String getId();
}
