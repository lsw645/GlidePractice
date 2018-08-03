package com.lxw.glide.load.model;

import com.lxw.glide.load.Encoder;
import com.lxw.glide.util.ByteArrayPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class StreamEncoder implements Encoder<InputStream> {
    private static final String TAG = "StramEncoder";

    /**
     * TODO 没看懂这里的 读写作用
     * @param data
     * @param os
     * @return
     */
    @Override
    public boolean encode(InputStream data, OutputStream os) {
        byte[] buffer = ByteArrayPool.get().getBytes();
        try {
            int read;
            while ((read = data.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            ByteArrayPool.get().releaseBytes(buffer);
        }
    }

    @Override
    public String getId() {
        return "";
    }
}
