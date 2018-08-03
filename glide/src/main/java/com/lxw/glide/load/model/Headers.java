package com.lxw.glide.load.model;

import java.util.Collections;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public interface Headers {

    Headers NONE = new Headers() {
        @Override
        public Map<String, String> getHeaders() {
            return Collections.EMPTY_MAP;
        }
    };
    Headers DEFAULT = new LazyHeaders.Builder().build();

    Map<String, String> getHeaders();
}
