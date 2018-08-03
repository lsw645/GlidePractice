package com.lxw.glide.load.model;

import android.net.Uri;
import android.text.TextUtils;

import com.lxw.glide.load.data.DataFetcher;

import java.io.File;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class StringLoader<T> implements ModelLoader<String, T> {
    private final ModelLoader<Uri, T> uriLoader;

    public StringLoader(ModelLoader<Uri, T> uriLoader) {
        this.uriLoader = uriLoader;
    }

    /**
     *   其实就是将字符串 转化为 Uri
     * @param model 传入的字符串
     * @param width 宽
     * @param height 高
     * @return 起来UriLoader
     */
    @Override
    public DataFetcher<T> getResourceFetcher(String model, int width, int height) {
        Uri uri;
        if (TextUtils.isEmpty(model)) {
            return null;
            //认为是 路径
        } else if (model.startsWith("/")) {
            uri = toFileUri(model);
        } else {
            uri = Uri.parse(model);
            final String scheme = uri.getScheme();
            if (scheme == null) {
                uri = toFileUri(model);
            }
        }

        return uriLoader.getResourceFetcher(uri,width,height);
    }

    private Uri toFileUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
