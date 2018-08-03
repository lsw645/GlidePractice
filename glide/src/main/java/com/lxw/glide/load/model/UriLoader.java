package com.lxw.glide.load.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.lxw.glide.load.data.DataFetcher;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public abstract class UriLoader<T> implements ModelLoader<Uri, T> {
    private final Context context;
    private final ModelLoader<GlideUrl, T> urlLoader;

    public UriLoader(Context context, ModelLoader<GlideUrl, T> urlLoader) {
        this.context = context;
        this.urlLoader = urlLoader;
    }


    @Override
    public DataFetcher<T> getResourceFetcher(Uri model, int width, int height) {
        final String scheme = model.getScheme();
        DataFetcher<T> result = null;
        if(isLocalUri(scheme)){

        }else if(urlLoader!=null&&("http".equals(scheme)||"https".equals(scheme))){
            result = urlLoader.getResourceFetcher(new GlideUrl(model.toString()), width, height);
        }
        return result;
    }

    private static boolean isLocalUri(String scheme) {
        return ContentResolver.SCHEME_FILE.equals(scheme)
                || ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme);
    }
}
