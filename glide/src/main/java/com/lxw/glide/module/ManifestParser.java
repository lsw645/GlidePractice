package com.lxw.glide.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/07/29
 *     desc   :
 * </pre>
 */
public final class ManifestParser {

    private static final String GLIDE_MODULE_NAME = "GlideModule";

    private final Context context;

    public ManifestParser(Context context) {
        this.context = context;
    }
    //从AndroidManifest中获取注册的 GlideModule
    public List<GlideModule> parse() {
        List<GlideModule> modules = new ArrayList<>();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                for (String key : appInfo.metaData.keySet()) {
                    if (GLIDE_MODULE_NAME.equals(appInfo.metaData.get(key))) {
                        modules.add(parseModule(key));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return modules;
    }

    //反射 创建 GlideMoudule;
    private GlideModule parseModule(String className) {
        Class<?> clazz;

        try {
            clazz = Class.forName(className);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to find GlideModule is impelementation", e);
        }

        Object glideModule = null;
        try {
            glideModule = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to instantiate GlideModule implementation for " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate GlideModule implementation for " + clazz, e);
        }

        if (!(glideModule instanceof GlideModule)) {
            throw new RuntimeException("Expected instanceof GlideModule, but found: \" + module");
        }

        return (GlideModule) glideModule;
    }

}
