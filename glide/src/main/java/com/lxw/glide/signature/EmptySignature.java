package com.lxw.glide.signature;

import com.lxw.glide.load.Key;

import java.security.MessageDigest;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class EmptySignature implements Key {
    private static final EmptySignature EMPTY_KEY = new EmptySignature();

   public static EmptySignature obtain(){
       return EMPTY_KEY;
   }
    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }

    private EmptySignature() {
    }
}
