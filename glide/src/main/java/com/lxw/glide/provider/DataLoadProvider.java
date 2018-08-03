package com.lxw.glide.provider;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;

import java.io.File;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface DataLoadProvider<T, Z> {
    //获取缓存的 解码器 ， 其实就是从  diskLruCache 里面获取，即file
    ResourceDecoder<File, Z> getCacheDecoder();
    //to use to decode the resource from the original data. 从原始数据中进行解码
    ResourceDecoder<T,Z> getSourceDecoder();
    //to use to write the original data to the disk cache. 将原始数据写入磁盘缓存中
    Encoder<T> getSourceEncoder();
    //to use to write the decoded and transformed resource
    //     * to the disk cache. 将解码和transformed后的数据写入磁盘缓存中
    ResourceEncoder<Z> getEncoder();

}
