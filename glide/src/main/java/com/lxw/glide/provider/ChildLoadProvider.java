package com.lxw.glide.provider;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.ResourceEncoder;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;

import java.io.File;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class ChildLoadProvider<ModelType, DataType, ResourceType, TranscodeType>
        implements LoadProvider<ModelType, DataType, ResourceType, TranscodeType> {
    private final LoadProvider<ModelType, DataType, ResourceType, TranscodeType> parent;

    public ChildLoadProvider(LoadProvider<ModelType, DataType, ResourceType, TranscodeType> parent) {
        this.parent = parent;
    }

    @Override
    public ModelLoader<ModelType, DataType> getModelLoader() {
        return null;
    }

    @Override
    public ResourceTranscoder<ResourceType, TranscodeType> getTranscoder() {
        return null;
    }

    @Override
    public ResourceDecoder<File, ResourceType> getCacheDecoder() {
        return null;
    }

    @Override
    public ResourceDecoder<DataType, ResourceType> getSourceDecoder() {
        return null;
    }

    @Override
    public Encoder<DataType> getSourceEncoder() {
        return null;
    }

    @Override
    public ResourceEncoder<ResourceType> getEncoder() {
        return null;
    }
}
