package com.lxw.glide.load.engine;

import com.lxw.glide.load.Key;
import com.lxw.glide.load.engine.cache.EngineJob;

public interface EngineJobListener {

    void onEngineJobComplete(Key key, EngineResource<?> resource);

    void onEngineJobCancelled(EngineJob engineJob, Key key);
}