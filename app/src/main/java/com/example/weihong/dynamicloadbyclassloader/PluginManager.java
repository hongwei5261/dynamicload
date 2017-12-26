package com.example.weihong.dynamicloadbyclassloader;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by weihong on 17-12-21.
 */

// 为调试通过
@Deprecated
public class PluginManager {

    private DexClassLoader mDexClassLoader;
    private File dexOutputDir;
    private String mDexPath;

    private static final class Holder {
        private static PluginManager INSTANCE = new PluginManager();
    }

    public static final PluginManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mDexPath = context.getCacheDir() + File.separator + "plugin-debug.apk";
        dexOutputDir = context.getDir("dex", 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDex();
            }
        }).start();
    }

    public DexClassLoader getDexClassLoader() {
        if (mDexClassLoader == null) {
            loadDex();
        }
        return mDexClassLoader;
    }

    public String getDexPath() {
        return mDexPath;
    }

    private void loadDex() {
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        mDexClassLoader = new DexClassLoader(mDexPath,
                dexOutputPath, null, localClassLoader);
    }


}
