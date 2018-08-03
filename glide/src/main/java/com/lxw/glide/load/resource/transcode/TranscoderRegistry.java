package com.lxw.glide.load.resource.transcode;

import com.lxw.glide.util.MultiClassKey;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class TranscoderRegistry {
    private static final MultiClassKey GET_KEY = new MultiClassKey();
    private final Map<MultiClassKey, ResourceTranscoder<?, ?>> factories = new HashMap<>();

    public <Z, R> void register(Class<Z> decodedClass, Class<R> transcodedClass, ResourceTranscoder<Z, R> transcoder) {
        factories.put(new MultiClassKey(decodedClass, transcodedClass), transcoder);
    }

    @SuppressWarnings("unchecked")
    public <Z, R> ResourceTranscoder<Z, R> get(Class<Z> decodedClass, Class<R> transcodedClass) {
        if (decodedClass.equals(transcodedClass)) {
            // we know they're the same type (Z and R)
            return (ResourceTranscoder<Z, R>) UnitTranscoder.get();
        }
        final ResourceTranscoder<?, ?> result;
        synchronized (GET_KEY) {
            GET_KEY.set(decodedClass, transcodedClass);
            result = factories.get(GET_KEY);
        }
        if (result == null) {
            throw new IllegalArgumentException("No transcoder registered for " + decodedClass + " and "
                    + transcodedClass);
        }
        return (ResourceTranscoder<Z, R>) result;
    }
}
