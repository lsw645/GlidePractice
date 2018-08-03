package com.lxw.glide.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class RequestManagerRetriever implements Handler.Callback {
    private static final RequestManagerRetriever INSTANCE = new RequestManagerRetriever();
    private final Map<android.app.FragmentManager, RequestFragment> pendingRequestManagers = new HashMap<>();
    private final Map<FragmentManager, RequestFragment> pendingSupportRequestManagers = new HashMap<>();
    private static final String FRAG_TAG = "com.lxw.glide.RequestFragment";
    private final Handler mHandler;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;

    private RequestManagerRetriever() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static RequestManagerRetriever get() {
        return INSTANCE;
    }


    public RequestManager get(FragmentActivity activity) {
        return fragmentGet(activity, activity.getSupportFragmentManager());
    }

    private RequestManager fragmentGet(Activity activity, FragmentManager fm) {
        RequestFragment requestFragment = getRequestFragment(fm);
        RequestManager requestManager = requestFragment.getRequestManager();
        if (requestManager == null) {
            requestManager = new RequestManager(activity, requestFragment.getLifecycles());
            requestFragment.setRequestManager(requestManager);
        }
        return requestManager;
    }

    private RequestFragment getRequestFragment(FragmentManager fm) {
        RequestFragment requestFragment = (RequestFragment) fm.findFragmentByTag(FRAG_TAG);
        if (requestFragment == null) {
            requestFragment = pendingRequestManagers.get(fm);
            if (requestFragment == null) {
                requestFragment = new RequestFragment();
                pendingSupportRequestManagers.put(fm, requestFragment);
                fm.beginTransaction().add(requestFragment, FRAG_TAG).commitAllowingStateLoss();
                mHandler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return requestFragment;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ID_REMOVE_FRAGMENT_MANAGER:
                android.app.FragmentManager fm = (android.app.FragmentManager) msg.obj;
                pendingRequestManagers.remove(fm);
                break;
            case ID_REMOVE_SUPPORT_FRAGMENT_MANAGER:
                android.support.v4.app.FragmentManager fm4 = (android.support.v4.app.FragmentManager) msg.obj;
                pendingSupportRequestManagers.remove(fm4);
                break;
            default:
                break;
        }

        return false;
    }
}
