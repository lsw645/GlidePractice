package com.lxw.glide.load.model.file_descriptor;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.GenericLoaderFactory;
import com.lxw.glide.load.model.ModelLoader;
import com.lxw.glide.load.model.ModelLoaderFactory;
import com.lxw.glide.load.model.StringLoader;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/01
 *     desc   :
 * </pre>
 */
public class FileDescriptorStringLoader extends StringLoader<ParcelFileDescriptor>
        implements ModelLoader<String, ParcelFileDescriptor> {


    public static class Factory implements ModelLoaderFactory<String,ParcelFileDescriptor>{

        @Override
        public ModelLoader<String, ParcelFileDescriptor> build(Context context, GenericLoaderFactory factories) {
            return new FileDescriptorStringLoader(factories.buildModelLoader(Uri.class,ParcelFileDescriptor.class));
        }

        @Override
        public void teardown() {

        }
    }


    public FileDescriptorStringLoader(Context context) {
        this(Glide.buildModelLoader(Uri.class, ParcelFileDescriptor.class, context));
    }

    public FileDescriptorStringLoader(ModelLoader<Uri, ParcelFileDescriptor> uriLoader) {
        super(uriLoader);
    }


}
