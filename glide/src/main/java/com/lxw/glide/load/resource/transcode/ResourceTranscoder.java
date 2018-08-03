package com.lxw.glide.load.resource.transcode;

import com.lxw.glide.load.engine.Resource;

public interface ResourceTranscoder<Z, R> {

    /**
     * Transcodes the given resource to the new resource type and returns the wew resource.
     *
     * @param toTranscode The resource to transcode.
     */
    Resource<R> transcode(Resource<Z> toTranscode);

    String getId();
}