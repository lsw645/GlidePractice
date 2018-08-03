package com.lxw.glide.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

/**
 * An {@link java.io.InputStream} that catches {@link java.io.IOException}s during read and skip calls and stores them
 * so they can later be handled or thrown. This class is a workaround for a framework issue where exceptions during
 * reads while decoding bitmaps in {@link android.graphics.BitmapFactory} can return partially decoded bitmaps.
 *
 * See https://github.com/bumptech/glide/issues/126.
 */
public class ExceptionCatchingInputStream extends InputStream {

    private static final Queue<ExceptionCatchingInputStream> QUEUE = Util.createQueue(0);

    private InputStream wrapped;
    private IOException exception;

    public static ExceptionCatchingInputStream obtain(InputStream toWrap) {
        ExceptionCatchingInputStream result;
        synchronized (QUEUE) {
            result = QUEUE.poll();
        }
        if (result == null) {
            result = new ExceptionCatchingInputStream();
        }
        result.setInputStream(toWrap);
        return result;
    }

    // Exposed for testing.
    static void clearQueue() {
        while (!QUEUE.isEmpty()) {
            QUEUE.remove();
        }
    }

    ExceptionCatchingInputStream() {
        // Do nothing.
    }

    void setInputStream(InputStream toWrap) {
        wrapped = toWrap;
    }

    @Override
    public int available() throws IOException {
        return wrapped.available();
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    @Override
    public void mark(int readlimit) {
        wrapped.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return wrapped.markSupported();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        int read;
        try {
            read = wrapped.read(buffer);
        } catch (IOException e) {
            exception = e;
            read = -1;
        }
        return read;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int read;
        try {
            read = wrapped.read(buffer, byteOffset, byteCount);
        } catch (IOException e) {
            exception = e;
            read = -1;
        }
        return read;
    }

    @Override
    public synchronized void reset() throws IOException {
        wrapped.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        long skipped;
        try {
            skipped = wrapped.skip(byteCount);
        } catch (IOException e) {
            exception = e;
            skipped = 0;
        }
        return skipped;
    }

    @Override
    public int read() throws IOException {
        int result;
        try {
            result = wrapped.read();
        } catch (IOException e) {
            exception = e;
            result = -1;
        }
        return result;
    }

    public IOException getException() {
        return exception;
    }

    public void release() {
        exception = null;
        wrapped = null;
        synchronized (QUEUE) {
            QUEUE.offer(this);
        }
    }
}
