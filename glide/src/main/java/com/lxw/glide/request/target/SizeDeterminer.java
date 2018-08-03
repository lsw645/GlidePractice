package com.lxw.glide.request.target;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public  class SizeDeterminer {
        // Some negative sizes (WRAP_CONTENT) are valid, 0 is never valid.
        private static final int PENDING_SIZE = 0;

        private final View view;
        private final List<SizeReadyCallback> cbs = new ArrayList<SizeReadyCallback>();

        private SizeDeterminerLayoutListener layoutListener;
        private Point displayDimens;

        public SizeDeterminer(View view) {
            this.view = view;
        }

        private void notifyCbs(int width, int height) {
            for (SizeReadyCallback cb : cbs) {
                cb.onSizeReady(width, height);
            }
            cbs.clear();
        }

        private void checkCurrentDimens() {
            if (cbs.isEmpty()) {
                return;
            }

            int currentWidth = getViewWidthOrParam();
            int currentHeight = getViewHeightOrParam();
            if (!isSizeValid(currentWidth) || !isSizeValid(currentHeight)) {
                return;
            }

            notifyCbs(currentWidth, currentHeight);
            // Keep a reference to the layout listener and remove it here
            // rather than having the observer remove itself because the observer
            // we add the listener to will be almost immediately merged into
            // another observer and will therefore never be alive. If we instead
            // keep a reference to the listener and remove it here, we get the
            // current view tree observer and should succeed.
            ViewTreeObserver observer = view.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnPreDrawListener(layoutListener);
            }
            layoutListener = null;
        }

        public void getSize(SizeReadyCallback cb) {
            int currentWidth = getViewWidthOrParam();
            int currentHeight = getViewHeightOrParam();
            if (isSizeValid(currentWidth) && isSizeValid(currentHeight)) {
                cb.onSizeReady(currentWidth, currentHeight);
            } else {
                // We want to notify callbacks in the order they were added and we only expect one or two callbacks to
                // be added a time, so a List is a reasonable choice.
                if (!cbs.contains(cb)) {
                    cbs.add(cb);
                }
                if (layoutListener == null) {
                    final ViewTreeObserver observer = view.getViewTreeObserver();
                    layoutListener = new SizeDeterminerLayoutListener(this);
                    observer.addOnPreDrawListener(layoutListener);
                }
            }
        }

        private int getViewHeightOrParam() {
            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (isSizeValid(view.getHeight())) {
                return view.getHeight();
            } else if (layoutParams != null) {
                return getSizeForParam(layoutParams.height, true /*isHeight*/);
            } else {
                return PENDING_SIZE;
            }
        }

        private int getViewWidthOrParam() {
            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (isSizeValid(view.getWidth())) {
                return view.getWidth();
            } else if (layoutParams != null) {
                return getSizeForParam(layoutParams.width, false /*isHeight*/);
            } else {
                return PENDING_SIZE;
            }
        }

        private int getSizeForParam(int param, boolean isHeight) {
            if (param == ViewGroup.LayoutParams.WRAP_CONTENT) {
                Point displayDimens = getDisplayDimens();
                return isHeight ? displayDimens.y : displayDimens.x;
            } else {
                return param;
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @SuppressWarnings("deprecation")
        private Point getDisplayDimens() {
            if (displayDimens != null) {
                return displayDimens;
            }
            WindowManager windowManager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                displayDimens = new Point();
                display.getSize(displayDimens);
            } else {
                displayDimens = new Point(display.getWidth(), display.getHeight());
            }
            return displayDimens;
        }

        private boolean isSizeValid(int size) {
            return size > 0 || size == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        private static class SizeDeterminerLayoutListener implements ViewTreeObserver.OnPreDrawListener {
            private static final String TAG = "SizeDeterminerLayoutListener";
            private final WeakReference<SizeDeterminer> sizeDeterminerRef;

            public SizeDeterminerLayoutListener(SizeDeterminer sizeDeterminer) {
                sizeDeterminerRef = new WeakReference<SizeDeterminer>(sizeDeterminer);
            }

            @Override
            public boolean onPreDraw() {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "OnGlobalLayoutListener called listener=" + this);
                }
                SizeDeterminer sizeDeterminer = sizeDeterminerRef.get();
                if (sizeDeterminer != null) {
                    sizeDeterminer.checkCurrentDimens();
                }
                return true;
            }
        }
    }