package com.lxw.glide.util;

import java.util.Queue;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public final class ByteArrayPool {
    private static final String TAG = "ByteArrayPool";

    private static final int TEMP_BYTES_SIZE = 64 * 1024;
    private static final int MAX_SIZE = 2 * 1048 * 1024;
    private static final int MAX_BYTE_ARRAY_COUNT = MAX_SIZE;

    private final Queue<byte[]> tempQueue = Util.createQueue(0);
    private static final ByteArrayPool BYTE_ARRAY_POOL = new ByteArrayPool();

    public static ByteArrayPool get() {
        return BYTE_ARRAY_POOL;
    }

    private ByteArrayPool() {

    }

    public void clear() {
        synchronized (tempQueue) {
            tempQueue.clear();
        }
    }

    public byte[] getBytes() {
        byte[] result;
        synchronized (tempQueue) {
            result = tempQueue.poll();
        }
        if (result == null) {
            result = new byte[TEMP_BYTES_SIZE];
        }
        return result;
    }

    public boolean releaseBytes(byte[] bytes) {
        if (bytes.length != TEMP_BYTES_SIZE) {
            return false;
        }
        boolean accepted = false;
        synchronized (tempQueue) {
            if (tempQueue.size() < MAX_BYTE_ARRAY_COUNT) {
                accepted = true;
                tempQueue.offer(bytes);
            }
        }
        return accepted;
    }
}
