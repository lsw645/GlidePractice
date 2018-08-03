package com.lxw.glide.load.engine.executor;

import java.util.concurrent.ThreadFactory;

/**
 * A {@link java.util.concurrent.ThreadFactory} that builds threads with priority
 * {@link android.os.Process#THREAD_PRIORITY_BACKGROUND}.
 */
public class DefaultThreadFactory implements ThreadFactory {
    int threadNum = 0;

    @Override
    public Thread newThread(Runnable runnable) {
        final Thread result = new Thread(runnable, "fifo-pool-thread-" + threadNum) {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                super.run();
            }
        };
        threadNum++;
        return result;
    }
}