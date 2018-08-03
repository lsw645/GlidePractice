package com.lxw.glide.manager;

import com.lxw.glide.util.Util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class ActivityFragmentLifecycle implements Lifecycle {
    private final Set<LifecycleListener> lifecycleListeners =
            Collections.newSetFromMap(new WeakHashMap<LifecycleListener, Boolean>());
    private boolean isStarted;
    private boolean isDestroyed;

    @Override
    public void addListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
        if (isDestroyed) {
            listener.onDestroy();
        } else if (isStarted) {
            listener.onStart();
        } else {
            listener.onStop();
        }
    }

    public void onStart() {
        isStarted = true;
        for (LifecycleListener lifecycleListener : Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStart();
        }
    }

    public void onStop() {
        isStarted = false;                                      //copy on write
        for (LifecycleListener lifecycleListener :  Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStop();
        }
    }

    public void onDetroy() {
        isDestroyed = true;
        for (LifecycleListener lifecycleListener :  Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onDestroy();
        }
    }
}
