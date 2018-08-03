package com.lxw.glide.load.engine;

import android.util.Log;

import com.lxw.glide.Priority;
import com.lxw.glide.load.Encoder;
import com.lxw.glide.load.Key;
import com.lxw.glide.load.data.DataFetcher;
import com.lxw.glide.load.engine.cache.DiskCache;
import com.lxw.glide.load.resource.transcode.ResourceTranscoder;
import com.lxw.glide.provider.DataLoadProvider;
import com.lxw.glide.util.LogTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class DecodeJob<DataType, ResourceType, TranscodeType> {
    private static final String TAG = "DecodeJob";
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();
    private final EngineKey resultKey;
    private final int width;
    private final int height;
    private final DataFetcher<DataType> fetcher;
    private final DataLoadProvider<DataType, ResourceType> loadProvider;
    private final ResourceTranscoder<ResourceType, TranscodeType> transcoder;
    private final DiskCacheStrategy diskCacheStrategy;
    private final Priority priority;
    private final FileOpener fileOpener;
    private volatile boolean isCancelled;
    private final DiskCacheProvider diskCacheProvider;

    public DecodeJob(EngineKey key, int width, int height,
                     DataFetcher<DataType> dataFetcher,
                     DataLoadProvider<DataType, ResourceType> loadProvider,
                     ResourceTranscoder<ResourceType, TranscodeType> transcoder,
                     DiskCacheProvider diskCacheProvider,
                     DiskCacheStrategy diskCacheStrategy,
                     Priority priority) {
        this(key, width, height, dataFetcher, loadProvider, transcoder, diskCacheProvider,
                diskCacheStrategy, priority, DEFAULT_FILE_OPENER);
    }

    public DecodeJob(EngineKey resultKey, int width, int height, DataFetcher<DataType> fetcher,
                     DataLoadProvider<DataType, ResourceType> loadProvider,
                     ResourceTranscoder<ResourceType, TranscodeType> transcoder,
                     DiskCacheProvider diskCacheProvider,
                     DiskCacheStrategy diskCacheStrategy, Priority priority,
                     FileOpener fileOpener) {
        this.resultKey = resultKey;
        this.width = width;
        this.height = height;
        this.fetcher = fetcher;
        this.loadProvider = loadProvider;
        this.transcoder = transcoder;
        this.diskCacheStrategy = diskCacheStrategy;
        this.priority = priority;
        this.fileOpener = fileOpener;
        this.diskCacheProvider = diskCacheProvider;
    }

    public Resource<TranscodeType> decodeFromSource() throws Exception {

        Resource<ResourceType> decoded = decodeSource();
        return transformEncodeAndTranscode(decoded);
    }

    private Resource<TranscodeType> transformEncodeAndTranscode(Resource<ResourceType> decoded) {

        long startTime = LogTime.getLogTime();
        writeTransformedToCache(decoded);
        Resource<TranscodeType> result = transcode(decoded);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Transcoded transformed from source", startTime);
        }
        return result;
    }

    private Resource<TranscodeType> transcode(Resource<ResourceType> transformed) {
        if (transformed == null) {
            return null;
        }
        return transcoder.transcode(transformed);
    }

    private void writeTransformedToCache(Resource<ResourceType> transformed) {
        //如果transformed的或者 缓存策略不一样的，则返回
        if (transformed == null || !diskCacheStrategy.cacheResult()) {
            return;
        }
        long startTime = LogTime.getLogTime();
        SourceWriter<Resource<ResourceType>> writer =
                new SourceWriter<Resource<ResourceType>>(loadProvider.getEncoder(), transformed);
        diskCacheProvider.getDiskCache().put(resultKey, writer);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Wrote transformed from source to cache", startTime);
        }
    }

    private Resource<ResourceType> decodeSource() throws Exception {
        Resource<ResourceType> decoded = null;
        try {
            final DataType data = fetcher.loadData(priority);
            if (isCancelled) {
                return null;
            }
            decoded = decodeFromSourceData(data);
        } finally {
            fetcher.cleanup();
        }
        return decoded;
    }

    private Resource<ResourceType> decodeFromSourceData(DataType data) throws Exception {
        final Resource<ResourceType> decoded;
        if (diskCacheStrategy.cacheSource()) {
            decoded = cacheAndDecodeSourceData(data);
        } else {
            decoded = loadProvider.getSourceDecoder().decode(data, width, height);
        }
        return decoded;
    }

    /**
     * 储存 源数据， 并进行解码
     *
     * @param data
     * @return
     */
    private Resource<ResourceType> cacheAndDecodeSourceData(DataType data) {
        return null;
    }

    public Resource<TranscodeType> decodeResultFromCache() throws Exception {
        if (!diskCacheStrategy.cacheResult()) {
            return null;
        }

        long startTime = LogTime.getLogTime();
        Resource<ResourceType> transformed = loadFromCache(resultKey);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Decoded transformed from cache", startTime);
        }

        startTime = LogTime.getLogTime();
        Resource<TranscodeType> result = transcode(transformed);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Transcoded transformed from cache", startTime);
        }

        return result;
    }

    private Resource<ResourceType> loadFromCache(Key resultKey) throws Exception {
        File file = diskCacheProvider.getDiskCache().get(resultKey);
        if (file == null) {
            return null;
        }
        Resource<ResourceType> result = null;
        try {
            result = loadProvider.getCacheDecoder().decode(file, width, height);
        } finally {
            if (result != null) {
                diskCacheProvider.getDiskCache().delete(resultKey);
            }
        }
        return result;
    }

    private void logWithTimeAndKey(String message, long startTime) {
        Log.v(TAG, message + " in " + LogTime.getElapsedMillis(startTime) + ", key: " + resultKey);
    }

    public Resource<TranscodeType> decodeSourceFromCache() throws Exception {
        if (!diskCacheStrategy.cacheSource()) {
            return null;
        }
        Resource<ResourceType> result = loadFromCache(resultKey.getOriginalKey());

        return transformEncodeAndTranscode(result);
    }

    class SourceWriter<DataType> implements DiskCache.Writer {

        private final Encoder<DataType> encoder;
        private final DataType data;

        public SourceWriter(Encoder<DataType> encoder, DataType data) {
            this.encoder = encoder;
            this.data = data;
        }

        @Override
        public boolean write(File file) {
            boolean success = false;
            OutputStream os = null;
            try {
                os = fileOpener.open(file);
                success = encoder.encode(data, os);
            } catch (FileNotFoundException e) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Failed to find file to write to disk cache", e);
                }
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // Do nothing.
                    }
                }
            }
            return success;
        }
    }


    interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    static class FileOpener {
        public OutputStream open(File file) throws FileNotFoundException {
            return new BufferedOutputStream(new FileOutputStream(file));
        }
    }
}
