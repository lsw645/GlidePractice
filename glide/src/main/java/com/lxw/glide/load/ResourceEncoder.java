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
public interface ResourceEncoder<T> extends Encoder<Resource<T>> {
    //控制Resource的类型   // specializing the generic arguments
}
