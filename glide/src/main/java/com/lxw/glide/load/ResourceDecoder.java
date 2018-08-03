package com.lxw.glide.load;

import com.lxw.glide.load.engine.Resource;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface ResourceDecoder<T, Z> {

    Resource<Z> decode(T source, int width, int height) throws Exception;

    String getId();

}
