package com.lxw.glide.load;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public interface Key {
    String STRING_CHARSET_NAME = "UTF-8";

    void updateDiskCacheKey(MessageDigest messageDigest)  throws UnsupportedEncodingException;;

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
