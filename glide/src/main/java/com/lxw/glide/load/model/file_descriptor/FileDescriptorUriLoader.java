package com.lxw.glide.load.model.file_descriptor;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.GenericLoaderFactory;
import com.lxw.glide.load.model.GlideUrl;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.model.ModelLoaderFactory;
import com.lxw.glide.load.model.UriLoader;


/**

 */
public class FileDescriptorUriLoader extends UriLoader<ParcelFileDescriptor> {

    /**
     * T
     */
    public static class Factory implements ModelLoaderFactory<Uri, ParcelFileDescriptor> {
        @Override
        public ModelLoader<Uri, ParcelFileDescriptor> build(Context context, GenericLoaderFactory factories) {
            return new FileDescriptorUriLoader(context, factories.buildModelLoader(GlideUrl.class,
                    ParcelFileDescriptor.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    public FileDescriptorUriLoader(Context context) {
        this(context, Glide.buildModelLoader(GlideUrl.class, ParcelFileDescriptor.class, context));
    }

    public FileDescriptorUriLoader(Context context, ModelLoader<GlideUrl, ParcelFileDescriptor> urlLoader) {
        super(context, urlLoader);
    }


}
