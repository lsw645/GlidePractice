package com.lxw.glide.load.resource.transcode;

import com.lxw.glide.load.engine.Resource;

public class UnitTranscoder<Z> implements ResourceTranscoder<Z, Z> {
    private static final UnitTranscoder<?> UNIT_TRANSCODER = new UnitTranscoder<Object>();

    @SuppressWarnings("unchecked")
    public static <Z> ResourceTranscoder<Z, Z> get() {
        return (ResourceTranscoder<Z, Z>) UNIT_TRANSCODER;
    }

    @Override
    public Resource<Z> transcode(Resource<Z> toTranscode) {
        return toTranscode;
    }

    @Override
    public String getId() {
        return "";
    }
}
