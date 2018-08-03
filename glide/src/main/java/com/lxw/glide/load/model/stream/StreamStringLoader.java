package com.lxw.glide.load.model.stream;

import android.content.Context;
import android.net.Uri;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.GenericLoaderFactory;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.model.ModelLoaderFactory;
import com.lxw.glide.load.model.StringLoader;

import java.io.InputStream;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class StreamStringLoader extends StringLoader<InputStream> {

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return  new StreamStringLoader(factories.buildModelLoader(Uri.class, InputStream.class));
//            return Glide.buildModelLoader(String.class, InputStream.class, context);
        }

        @Override
        public void teardown() {

        }
    }

    public StreamStringLoader(Context context) {
        this(Glide.buildModelLoader(Uri.class, InputStream.class, context));
    }


    public StreamStringLoader(ModelLoader<Uri, InputStream> uriLoader) {
        super(uriLoader);
    }
}
