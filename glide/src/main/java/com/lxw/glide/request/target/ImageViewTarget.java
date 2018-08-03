package com.lxw.glide.request.target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public abstract class ImageViewTarget<Z> extends ViewTarget<ImageView, Z> {
    public ImageViewTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        view.setImageDrawable(errorDrawable);
    }

    @Override
    public void onResourceReady(Z resource) {
        setResource(resource);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

    protected abstract void setResource(Z resource);
}
