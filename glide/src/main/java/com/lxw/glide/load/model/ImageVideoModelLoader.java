package com.lxw.glide.load.model;

import android.os.ParcelFileDescriptor;

import com.lxw.glide.Priority;
import com.lxw.glide.load.data.DataFetcher;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class ImageVideoModelLoader<ModelType> implements ModelLoader<ModelType, ImageVideoWrapper> {
    private static final String TAG = "IVML";
    private final ModelLoader<ModelType, InputStream> streamLoader;
    private final ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorLoader;

    public ImageVideoModelLoader(ModelLoader<ModelType, InputStream> streamLoader,
                                 ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorLoader) {

        if (streamLoader == null && fileDescriptorLoader == null) {
            throw new NullPointerException("At least one of streamLoader and fileDescriptorLoader must be non null");
        }
        this.streamLoader = streamLoader;
        this.fileDescriptorLoader = fileDescriptorLoader;
    }

    @Override
    public DataFetcher<ImageVideoWrapper> getResourceFetcher(ModelType model, int width, int height) {
        DataFetcher<InputStream> streamFetcher = null;

        if (streamLoader != null) {
            streamFetcher = streamLoader.getResourceFetcher(model, width, height);
        }

        DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher = null;
        if (fileDescriptorLoader != null) {
            fileDescriptorFetcher = fileDescriptorLoader.getResourceFetcher(model, width, height);
        }

//        if (streamFetcher != null && fileDescriptorFetcher != null) {
//            return new ImageVideoFetcher(streamFetcher, fileDescriptorFetcher);
//        } else {
//            return null;
//        }
        return new ImageVideoFetcher(streamFetcher, fileDescriptorFetcher);
    }

    static class ImageVideoFetcher implements DataFetcher<ImageVideoWrapper> {
        private final DataFetcher<InputStream> streamFetcher;
        private final DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher;

        public ImageVideoFetcher(DataFetcher<InputStream> streamFetcher,
                                 DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher) {
            this.streamFetcher = streamFetcher;
            this.fileDescriptorFetcher = fileDescriptorFetcher;
        }

        @Override
        public ImageVideoWrapper loadData(Priority priority) throws Exception {
            InputStream is = null;
            if (streamFetcher != null) {
                is = streamFetcher.loadData(priority);
            }
            ParcelFileDescriptor fileDescriptor = null;
            if (fileDescriptorFetcher != null) {
                fileDescriptor = fileDescriptorFetcher.loadData(priority);
            }
            return new ImageVideoWrapper(is, fileDescriptor);
        }

        @Override
        public void cleanup() {
            //TODO: what if this throws?
            if (streamFetcher != null) {
                streamFetcher.cleanup();
            }
            if (fileDescriptorFetcher != null) {
                fileDescriptorFetcher.cleanup();
            }
        }

        @Override
        public String getId() {
            if (streamFetcher != null) {
                return streamFetcher.getId();
            } else {
                return fileDescriptorFetcher.getId();
            }
        }

        @Override
        public void cancel() {
            if (streamFetcher != null) {
                streamFetcher.cancel();
            }
            if (fileDescriptorFetcher != null) {
                fileDescriptorFetcher.cancel();
            }
        }
    }
}
