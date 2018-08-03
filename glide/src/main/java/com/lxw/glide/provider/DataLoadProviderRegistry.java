package com.lxw.glide.provider;

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
public class DataLoadProviderRegistry {
    private static final MultiClassKey GET_KEY
            = new MultiClassKey();

    private final Map<MultiClassKey, DataLoadProvider<?, ?>> providers =
            new HashMap<>();

    public <DataType, ResourceType> void register(Class<DataType> dataClass,
                                                  Class<ResourceType> resourceClass,
                                                  DataLoadProvider<DataType, ResourceType> dataLoadProvider) {
        providers.put(new MultiClassKey(dataClass, resourceClass), dataLoadProvider);
    }
    @SuppressWarnings("unchecked")
    public <DataType, ResourceType> DataLoadProvider<DataType, ResourceType> get(Class<DataType> dataClass, Class<ResourceType> resourceClass) {
        DataLoadProvider<?, ?> result;
        //这里重写了 MultiClassKey 的equal跟hashcode方法，所以 只是用一个GET_KEY作为map key的载体
        synchronized (GET_KEY) {
            GET_KEY.set(dataClass, resourceClass);
            result = providers.get(GET_KEY);
        }
        if (result == null) {
            result = EmptyDataLoadProvider.get();
        }
        return (DataLoadProvider<DataType, ResourceType>) result;
    }
}
