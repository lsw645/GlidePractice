package com.lxw.glide.load.engine;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.Key;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class EngineKeyFactory {

    EngineKey build(String id, Key signature, int width, int height, ResourceDecoder cacheDecoder,
                    ResourceDecoder sourceDecoder, ResourceEncoder encoder,
                    ResourceTranscoder transcoder, Encoder sourceEncoder) {
        return new EngineKey(id, signature, width, height, cacheDecoder,
                sourceDecoder, encoder, transcoder, sourceEncoder);
    }

}
