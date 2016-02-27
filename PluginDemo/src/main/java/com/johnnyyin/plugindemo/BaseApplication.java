package com.johnnyyin.plugindemo;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        InstrumentationHook.inject();
    }

}
