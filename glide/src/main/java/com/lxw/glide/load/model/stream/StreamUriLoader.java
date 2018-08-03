package com.lxw.glide.load.model.stream;

import android.content.Context;
import android.net.Uri;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.GenericLoaderFactory;
import com.lxw.glide.load.model.GlideUrl;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.model.ModelLoaderFactory;
import com.lxw.glide.load.model.UriLoader;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class StreamUriLoader extends UriLoader<InputStream> {

    public StreamUriLoader(Context context) {
        this(context, Glide.buildModelLoader(GlideUrl.class, InputStream.class, context));
    }

    public StreamUriLoader(Context context, ModelLoader<GlideUrl, InputStream> urlLoader) {
        super(context, urlLoader);
    }

    public static class Factory implements ModelLoaderFactory<Uri,InputStream>{

        @Override
        public ModelLoader<Uri, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new StreamUriLoader(context);
        }

        @Override
        public void teardown() {

        }
    }

}
