package com.lxw.glide.load.engine;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public enum DecodeFormat {
    //888
    ALWAYS_ARGB_8888,
   // 888
    PREFER_ARGB_8888,
    //565
    PREFER_RGB_565;

    public static final DecodeFormat DEFAULT = DecodeFormat.PREFER_RGB_565;
}
