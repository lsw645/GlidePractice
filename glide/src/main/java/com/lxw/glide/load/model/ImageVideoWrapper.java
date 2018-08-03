package com.lxw.glide.load.model;

import android.os.ParcelFileDescriptor;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class ImageVideoWrapper {
    private final InputStream streamData;
    private final ParcelFileDescriptor fileDescriptor;

    public ImageVideoWrapper(InputStream streamData, ParcelFileDescriptor fileDescriptor) {
        this.streamData = streamData;
        this.fileDescriptor = fileDescriptor;
    }

    public InputStream getStreamData() {
        return streamData;
    }

    public ParcelFileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }
}
