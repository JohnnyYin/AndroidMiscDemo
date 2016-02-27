package com.johnnyyin.plugindemo;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InstrumentationHook extends Instrumentation {

    public static void inject() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Object sCurrentActivityThreadObj;
            if (Build.VERSION.SDK_INT < 11) {
                Method currentActivityThread = ActivityThread.getDeclaredMethod("currentActivityThread");
                currentActivityThread.setAccessible(true);
                sCurrentActivityThreadObj = currentActivityThread.invoke(null);
            } else {
                Field sCurrentActivityThread = ActivityThread.getDeclaredField("sCurrentActivityThread");
                sCurrentActivityThread.setAccessible(true);
                sCurrentActivityThreadObj = sCurrentActivityThread.get(null);
            }
            Field mInstrumentation = ActivityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            mInstrumentation.set(sCurrentActivityThreadObj, new InstrumentationHook());
            Log.d("SS", "BaseApplication.injectInstrumentation: ok");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d("SS", "BaseApplication.injectInstrumentation: exception = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.d("SS", "InstrumentationHook.newActivity:" + className);
        return super.newActivity(cl, className, intent);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        Log.d("SS", "InstrumentationHook.newActivity:" + clazz);
        return super.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Log.d("SS", "InstrumentationHook.callActivityOnCreate:" + activity);
        super.callActivityOnCreate(activity, icicle);
    }


    @Override
    public void callActivityOnStart(Activity activity) {
        Log.d("SS", "InstrumentationHook.callActivityOnStart:" + activity);
        super.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        Log.d("SS", "InstrumentationHook.callActivityOnResume:" + activity);
        super.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        Log.d("SS", "InstrumentationHook.callActivityOnPause:" + activity);
        super.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        Log.d("SS", "InstrumentationHook.callActivityOnStop:" + activity);
        super.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        Log.d("SS", "InstrumentationHook.callActivityOnDestroy:" + activity);
        super.callActivityOnDestroy(activity);
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        Log.d("SS", "InstrumentationHook.onException:" + e);
        return super.onException(obj, e);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        Log.d("SS", "InstrumentationHook.startActivitySync:" + intent);
        return super.startActivitySync(intent);
    }
}
