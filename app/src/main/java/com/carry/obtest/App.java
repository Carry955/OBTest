package com.carry.obtest;

import android.app.Application;

public class App extends Application {
    public static final String TAG = "ObjectBox";

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
