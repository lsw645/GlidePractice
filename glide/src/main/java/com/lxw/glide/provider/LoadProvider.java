package com.lxw.glide.provider;

import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public interface LoadProvider<ModelType, DataType, ResourceType, TranscodeType>
        extends DataLoadProvider<DataType, ResourceType> {
    // 获取 数据加载器
    ModelLoader<ModelType, DataType> getModelLoader();
    // 获取转码器
    ResourceTranscoder<ResourceType, TranscodeType> getTranscoder();
}
