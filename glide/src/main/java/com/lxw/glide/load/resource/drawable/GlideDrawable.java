package com.lxw.glide.load.resource.drawable;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/30
 *     desc   :
 * </pre>
 */
public abstract class GlideDrawable extends Drawable implements Animatable {

    public static final int LOOP_FOREVER = -1;

    public static final int LOOP_INTRINSIC = 0;

    public abstract boolean isAnimated();

    public abstract void setLoopCount(int loopCount);

}
