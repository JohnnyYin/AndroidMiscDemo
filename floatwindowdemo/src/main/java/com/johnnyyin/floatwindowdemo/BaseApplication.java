package com.johnnyyin.floatwindowdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Johnny on 15/9/19.
 */
public class BaseApplication extends Application {
    private static BaseApplication sInstnace;

    public BaseApplication() {
        sInstnace = this;
    }

    public static Context getInst() {
        return sInstnace;
    }
}
