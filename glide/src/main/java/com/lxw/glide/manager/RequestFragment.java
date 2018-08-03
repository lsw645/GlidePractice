package com.lxw.glide.manager;


import android.support.v4.app.Fragment;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public class RequestFragment extends Fragment {
    //一个Actvitiy绑定一个RequestManger 与一个 ReqeuetFragment  ,
    // RequestManager管理同一Activity的多个Request
    private RequestManager requestManager;
    //持有这个 lifecycle，监听Fragment的生命周期
    private ActivityFragmentLifecycle lifecycle;

    public RequestFragment() {
        lifecycle = new ActivityFragmentLifecycle();
    }


//    public ActivityFragmentLifecycle getLifecycle() {
//        return lifecycle;
//    }



    public ActivityFragmentLifecycle getLifecycles() {
        return lifecycle;
    }

    public void setLifecycle(ActivityFragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }


    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDetroy();
    }
}
