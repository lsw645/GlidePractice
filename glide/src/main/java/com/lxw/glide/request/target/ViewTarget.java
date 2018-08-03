package com.lxw.glide.request.target;

import android.view.View;

import com.lxw.glide.request.Request;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public abstract class ViewTarget<T extends View, Z> implements Target<Z> {

    protected final T view;
    private static boolean isTagUsedAtLeastOnce = false;
    private final SizeDeterminer sizeDeterminer;


    public ViewTarget(T view) {
        if (view == null) {
            throw new NullPointerException();
        }
        this.view = view;
        sizeDeterminer = new SizeDeterminer(view);
    }

    public T getView() {
        return view;
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        sizeDeterminer.getSize(cb);
    }

    @Override
    public void setRequest(Request request) {
        setTag(request);
    }

    private void setTag(Object tag) {
        isTagUsedAtLeastOnce = true;
        view.setTag(tag);
    }


    @Override
    public Request getRequest() {
        Object tag = getTag();
        Request request = null;
        if (tag != null) {
            if (tag instanceof Request) {
                request = (Request) tag;
            } else {
                throw new IllegalArgumentException("you can not use tag,glide is used");
            }
        }
        return request;
    }

    private Object getTag() {
        return view.getTag();
    }

    @Override
    public String toString() {
        return "Target for: " + view;
    }

}
