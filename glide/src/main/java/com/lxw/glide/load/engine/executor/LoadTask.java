package com.lxw.glide.load.engine.executor;

import java.util.concurrent.FutureTask;

// Visible for testing.
class LoadTask<T> extends FutureTask<T> implements Comparable<LoadTask<?>> {
    private final int priority;
    private final int order;

    public LoadTask(Runnable runnable, T result, int order) {
        super(runnable, result);
        if (!(runnable instanceof Prioritized)) {
            throw new IllegalArgumentException("FifoPriorityThreadPoolExecutor must be given Runnables that "
                    + "implement Prioritized");
        }
        priority = ((Prioritized) runnable).getPriority();
        this.order = order;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o instanceof LoadTask) {
            LoadTask<Object> other = (LoadTask<Object>) o;
            return order == other.order && priority == other.priority;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = priority;
        result = 31 * result + order;
        return result;
    }

    @Override
    public int compareTo(LoadTask<?> loadTask) {
        int result = priority - loadTask.priority;
        if (result == 0) {
            result = order - loadTask.order;
        }
        return result;
    }
}