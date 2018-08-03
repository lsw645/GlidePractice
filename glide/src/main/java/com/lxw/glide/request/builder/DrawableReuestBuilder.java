package com.lxw.glide.request.builder;

import android.content.Context;
import android.widget.ImageView;

import com.lxw.glide.Glide;
import com.lxw.glide.load.model.ImageVideoWrapper;
import com.lxw.glide.load.resource.drawable.GlideDrawable;
import com.lxw.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.lxw.glide.manager.Lifecycle;
import com.lxw.glide.manager.RequestTracker;
import com.lxw.glide.provider.LoadProvider;
import com.lxw.glide.request.target.Target;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class DrawableReuestBuilder<ModelType>
        extends GenericRequestBuilder<ModelType, ImageVideoWrapper, GifBitmapWrapper, GlideDrawable> {

    public DrawableReuestBuilder(Class<ModelType> modelClass, Context context, Glide glide,
                                 LoadProvider<ModelType, ImageVideoWrapper, GifBitmapWrapper, GlideDrawable> loadProvider,
                                 RequestTracker requestTracker, Lifecycle lifecycle) {
        super(context, modelClass, GlideDrawable.class, glide, loadProvider,requestTracker, lifecycle);
    }

    @Override
    public DrawableReuestBuilder<ModelType> load(ModelType modelType) {
        super.load(modelType);
        return this;
    }


    @Override
    public Target<GlideDrawable> into(ImageView view) {
        return super.into(view);
    }


}
