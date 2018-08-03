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
public class FixedLoadProvider<ModelType, DataType, ResourceType, TranscodeType>
        implements LoadProvider<ModelType, DataType, ResourceType, TranscodeType> {
    private final ModelLoader<ModelType, DataType> modelLoader;
    private final ResourceTranscoder<ResourceType, TranscodeType> transcoder;
    private final DataLoadProvider<DataType, ResourceType> dataLoadProvider;

    public FixedLoadProvider(ModelLoader<ModelType, DataType> modelLoader,
                             ResourceTranscoder<ResourceType, TranscodeType> transcoder,
                             DataLoadProvider<DataType, ResourceType> dataLoadProvider) {
        if (modelLoader == null) {
            throw new NullPointerException("ModelLoader must not be null");
        }
        this.modelLoader = modelLoader;

        if (transcoder == null) {
            throw new NullPointerException("Transcoder must not be null");
        }
        this.transcoder = transcoder;

        if (dataLoadProvider == null) {
            throw new NullPointerException("DataLoadProvider must not be null");
        }
        this.dataLoadProvider = dataLoadProvider;
    }

    @Override
    public ModelLoader<ModelType, DataType> getModelLoader() {
        return modelLoader;
    }

    @Override
    public ResourceTranscoder<ResourceType, TranscodeType> getTranscoder() {
        return  transcoder;
    }

    @Override
    public ResourceDecoder<File, ResourceType> getCacheDecoder() {
        return dataLoadProvider.getCacheDecoder();
    }

    @Override
    public ResourceDecoder<DataType, ResourceType> getSourceDecoder() {
        return dataLoadProvider.getSourceDecoder();
    }

    @Override
    public Encoder<DataType> getSourceEncoder() {
        return  dataLoadProvider.getSourceEncoder();
    }

    @Override
    public ResourceEncoder<ResourceType> getEncoder() {
        return dataLoadProvider.getEncoder();
    }
}
