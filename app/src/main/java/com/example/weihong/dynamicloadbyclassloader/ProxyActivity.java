package com.example.weihong.dynamicloadbyclassloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

import static com.example.weihong.dynamicloadbyclassloader.PluginConstant.EXTRA_DEX_PATH_KEY;
import static com.example.weihong.dynamicloadbyclassloader.PluginConstant.EXTRA_PLUGIN_CLASS_KEY;

/**
 * Created by weihong on 17-12-19.
 */

public class ProxyActivity extends Activity {

    private static final String TAG = "ProxyActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_EXTERNAL = 0;
    public static final int FROM_INTERNAL = 1;
    /**
     * 处理生命周期
     */
    HashMap<String, Method> mActivityLifecircleMethods = new HashMap<>();
    AssetManager mAssetManager;
    Resources mResources;
    Resources.Theme mTheme;
    DexClassLoader mDexClassLoader;
    private String mPluginClass;
    private String mDexPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH_KEY);
        mPluginClass = getIntent().getStringExtra(EXTRA_PLUGIN_CLASS_KEY);

        Log.d(TAG, "mClass=" + mPluginClass + " mDexPath=" + mDexPath);
        if (mPluginClass == null) {
            launchTargetActivity();
        } else {
            launchTargetActivity(mPluginClass);
        }
    }

    @SuppressLint("NewApi")
    protected void launchTargetActivity() {
        // 1.读取apk包信息
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(
                mDexPath, PackageManager.GET_ACTIVITIES);
        if ((packageInfo.activities != null)
                && (packageInfo.activities.length > 0)) {
            String activityName = packageInfo.activities[0].name;
            mPluginClass = activityName;
            launchTargetActivity(mPluginClass);
        }
    }

    Object mRemoteActivity;

    @SuppressLint("NewApi")
    protected void launchTargetActivity(final String className) {
        Log.d(TAG, "start launchTargetActivity, className=" + className);

        // 加载资源
        loadResources();
        // 2.加载插件apk或dex
        File dexOutputDir = this.getDir("dex", 0);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        mDexClassLoader = new DexClassLoader(mDexPath, dexOutputPath, null, localClassLoader);
        try {
            Class<?> localClass = mDexClassLoader.loadClass(className);
            Constructor<?> localConstructor = localClass
                    .getConstructor(new Class[]{});
            mRemoteActivity = localConstructor.newInstance(new Object[]{});
            Log.d(TAG, "instance = " + mRemoteActivity);
            // 初始化生命周期
            instantiateLifecircleMethods(localClass);

            Method setProxy = localClass.getMethod("setProxy",
                    new Class[]{Activity.class});
            setProxy.setAccessible(true);
            setProxy.invoke(mRemoteActivity, new Object[]{this});

            Method onCreate = localClass.getDeclaredMethod("onCreate",
                    new Class[]{Bundle.class});
            onCreate.setAccessible(true);
            Bundle bundle = new Bundle();
            bundle.putInt(FROM, FROM_EXTERNAL);
            bundle.putString(PluginConstant.EXTRA_DEX_PATH_KEY, mDexPath);
            bundle.putString(PluginConstant.EXTRA_PLUGIN_CLASS_KEY, className);
            bundle.putString(PluginConstant.EXTRA_PROXY_CLASS_KEY, ProxyActivity.class.getName());
            onCreate.invoke(mRemoteActivity, new Object[]{bundle});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }


    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Method onResume = mActivityLifecircleMethods.get(mRemoteActivity.getClass().getSimpleName() + "onResume");
        if (onResume != null) {
            try {
                onResume.invoke(mRemoteActivity, new Object[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        Method onPause = mActivityLifecircleMethods.get(mRemoteActivity.getClass().getSimpleName() + "onPause");
        if (onPause != null) {
            try {
                onPause.invoke(mRemoteActivity, new Object[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    protected void instantiateLifecircleMethods(Class<?> localClass) {
        String[] methodNames = new String[]{
                "onRestart",
                "onStart",
                "onResume",
                "onPause",
                "onStop",
                "onDestory"
        };
        for (String methodName : methodNames) {
            Method method = null;
            try {
                method = localClass.getDeclaredMethod(methodName, new Class[]{});
                method.setAccessible(true);
                mActivityLifecircleMethods.put(localClass.getSimpleName() + methodName, method);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        Method onCreate = null;
        try {
            onCreate = localClass.getDeclaredMethod("onCreate", new Class[]{Bundle.class});
            onCreate.setAccessible(true);
            mActivityLifecircleMethods.put(localClass.getSimpleName() + "onCreate", onCreate);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Method onActivityResult = null;
        try {
            onActivityResult = localClass.getDeclaredMethod("onActivityResult",
                    new Class[]{int.class, int.class, Intent.class});
            onActivityResult.setAccessible(true);
            mActivityLifecircleMethods.put(localClass.getSimpleName() + "onActivityResult", onActivityResult);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}