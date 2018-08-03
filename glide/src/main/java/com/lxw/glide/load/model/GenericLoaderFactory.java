package com.lxw.glide.load.model;

import android.content.Context;

import com.lxw.glide.load.data.DataFetcher;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/31
 *     desc   :
 * </pre>
 */
public class GenericLoaderFactory {

    private final Map<Class, Map<Class, ModelLoaderFactory>> modelClassToResourceFactories =
            new HashMap<>();
    private final Map<Class, Map<Class, ModelLoader>> cacheModelLoaders = new HashMap<>();

    private static final ModelLoader NULL_MODELOADER = new ModelLoader() {
        @Override
        public DataFetcher getResourceFetcher(Object model, int width, int height) {
            throw new NoSuchMethodError("This should never be called!");
        }

        @Override
        public String toString() {
            return "NULL_MODEL_LOADER";
        }
    };

    private final Context context;

    public GenericLoaderFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized <ModelType, ResourceType> ModelLoaderFactory<ModelType, ResourceType> register(
            Class<ModelType> model,
            Class<ResourceType> resource,
            ModelLoaderFactory<ModelType, ResourceType> factory) {
        cacheModelLoaders.clear();
        Map<Class, ModelLoaderFactory> factoryMap = modelClassToResourceFactories.get(model);
        if (factoryMap == null) {
            factoryMap = new HashMap<>();
            modelClassToResourceFactories.put(model, factoryMap);
        }
        ModelLoaderFactory previous = factoryMap.put(resource, factory);
        if (previous != null) {
            for (Map<Class, ModelLoaderFactory> modelLoaderFactoryMap : modelClassToResourceFactories.values()) {
                if (modelLoaderFactoryMap.containsValue(previous)) {
                    previous = null;
                    break;
                }
            }
        }
        return previous;
    }


    public synchronized <ModelType, ResourceType> ModelLoaderFactory<ModelType, ResourceType> unregister(
            Class<ModelType> model, Class<ResourceType> resource) {
        cacheModelLoaders.clear();
        Map<Class, ModelLoaderFactory> factoryMap = modelClassToResourceFactories.get(model);
        ModelLoaderFactory result = null;
        if (factoryMap != null) {
            result = factoryMap.remove(resource);
        }
        return result;
    }

    public <ModelType, ResourceType> ModelLoader<ModelType, ResourceType> buildModelLoader(
            Class<ModelType> model,
            Class<ResourceType> resource) {
        ModelLoader<ModelType, ResourceType> result = getCacheLoader(model, resource);
        if (result != null) {
            if (NULL_MODELOADER.equals(result)) {
                return null;
            } else {
                return result;
            }
        }

        final ModelLoaderFactory<ModelType, ResourceType> factory = getFactory(model, resource);
        //判断factory是否为空，储存NULL_MODELOADER
        if (factory != null) {
            //TODO what is this;
            result = factory.build(context, this);
            cacheModelLoader(model, resource, result);
        } else {
            cacheNullLoader(model, resource);
        }
        return result;
    }

    private <ModelType, ResourceType> void cacheNullLoader(Class<ModelType> model, Class<ResourceType> resource) {
        cacheModelLoader(model, resource, NULL_MODELOADER);
    }

    private <ModelType, ResourceType> void cacheModelLoader(Class<ModelType> model, Class<ResourceType> resource, ModelLoader modelLoader) {
        Map<Class, ModelLoader> resourceToLoaders = cacheModelLoaders.get(model);
        if (resourceToLoaders == null) {
            resourceToLoaders = new HashMap<>();
            cacheModelLoaders.put(model, resourceToLoaders);
        }
        resourceToLoaders.put(resource, modelLoader);
    }


    private <T, Y> ModelLoaderFactory<T, Y> getFactory(Class<T> modelClass, Class<Y> resourceClass) {
        Map<Class/*Y*/, ModelLoaderFactory/*T, Y*/> resourceToFactories = modelClassToResourceFactories.get(modelClass);
        ModelLoaderFactory/*T, Y*/ result = null;
        if (resourceToFactories != null) {
            result = resourceToFactories.get(resourceClass);
        }

        if (result == null) {
            for (Class<? super T> registeredModelClass : modelClassToResourceFactories.keySet()) {
                // This accounts for model subclasses, our map only works for exact matches. We should however still
                // match a subclass of a model with a factory for a super class of that model if if there isn't a
                // factory for that particular subclass. Uris are a great example of when this happens, most uris
                // are actually subclasses for Uri, but we'd generally rather load them all with the same factory rather
                // than trying to register for each subclass individually.
                if (registeredModelClass.isAssignableFrom(modelClass)) {
                    Map<Class/*Y*/, ModelLoaderFactory/*T, Y*/> currentResourceToFactories =
                            modelClassToResourceFactories.get(registeredModelClass);
                    if (currentResourceToFactories != null) {
                        result = currentResourceToFactories.get(resourceClass);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

//    private <T, Y> void cacheNullLoader(Class<T> modelClass, Class<Y> resourceClass) {
//        cacheModelLoader(modelClass, resourceClass, NULL_MODEL_LOADER);
//    }

    private <ModelType, ResourceType> ModelLoader<ModelType, ResourceType> getCacheLoader(
            Class<ModelType> model,
            Class<ResourceType> resource) {
        Map<Class, ModelLoader> resourceToLoader = cacheModelLoaders.get(model);
        ModelLoader result = null;
        if (resourceToLoader != null) {
            result = resourceToLoader.get(resource);
        }
        return result;
    }


}
