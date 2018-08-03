package com.lxw.glide.load.resource.file;

import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.engine.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class FileToStreamDecoder<T> implements ResourceDecoder<File, T> {
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();
    private ResourceDecoder<InputStream, T> streamDecoder;
    private final FileOpener fileOpener;

    public FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder) {
        this(streamDecoder, DEFAULT_FILE_OPENER);
    }

    public FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder, FileOpener fileOpener) {
        this.streamDecoder = streamDecoder;
        this.fileOpener = fileOpener;
    }

    @Override
    public Resource<T> decode(File source, int width, int height) throws Exception {
        InputStream is = null;
        Resource<T> result = null;
        try{
            is =fileOpener.open(source);
            result = streamDecoder.decode(is,width,height);
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public String getId() {
        return "";
    }

    static class FileOpener {
        public InputStream open(File file) throws FileNotFoundException {
            return new FileInputStream(file);
        }
    }
}
