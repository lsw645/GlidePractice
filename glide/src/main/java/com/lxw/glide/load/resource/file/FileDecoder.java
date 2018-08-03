package com.lxw.glide.load.resource.file;


import com.lxw.glide.load.ResourceDecoder;
import com.lxw.glide.load.engine.Resource;

import java.io.File;

/**
 */
public class FileDecoder implements ResourceDecoder<File, File> {

    @Override
    public Resource<File> decode(File source, int width, int height) {
        return new FileResource(source);
    }

    @Override
    public String getId() {
        return "";
    }
}
