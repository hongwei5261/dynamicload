package com.example.weihong.dynamicloadbyclassloader;

import android.app.Application;

/**
 * Created by weihong on 17-12-21.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PluginManager.getInstance().init(this);
    }
}
