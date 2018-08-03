package com.lxw.glide.request.target;

import android.widget.ImageView;

import com.lxw.glide.load.resource.drawable.GlideDrawable;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public class GlideDrawableImageViewTarget extends ImageViewTarget<GlideDrawable> {

    public GlideDrawableImageViewTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onResourceReady(GlideDrawable resource) {
        super.onResourceReady(resource);
    }

    @Override
    protected void setResource(GlideDrawable resource) {
        view.setImageDrawable(resource);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
