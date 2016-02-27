package com.johnnyyin.systemapifix;

import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class SafeMediaPlayer extends MediaPlayer {

    public SafeMediaPlayer() {
        super();
    }

    public static void fix(MediaPlayer mp) {
        try {
            // 先修改系统类的ClassLoader
            try {
                Log.e("SS", android.media.MediaPlayer.class.getClassLoader() + "");
                Class s = Class.forName("android.media.MediaPlayer$EventHandler", true, SafeMediaPlayer.class.getClassLoader());
                Field classLoader = Class.class.getDeclaredField("classLoader");
                classLoader.setAccessible(true);
                classLoader.set(s, SafeMediaPlayer.class.getClassLoader());
                Log.e("SS", "SafeMediaPlayer.class.getClassLoader() = " + SafeMediaPlayer.class.getClassLoader() + "");
                Log.e("SS", "EventHandler.getClassLoader() = " + s.getClassLoader() + "");
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e("SS", "" + Log.getStackTraceString(e));
            }

            Looper looper;
            if ((looper = Looper.myLooper()) != null) {
//            mEventHandler = new EventHandler(this, looper);
            } else if ((looper = Looper.getMainLooper()) != null) {
//            mEventHandler = new EventHandler(this, looper);
            } else {
//            mEventHandler = null;
            }
            if (looper != null) {
                Class s = Class.forName("android.media.MediaPlayer$EventHandler", true, SafeMediaPlayer.class.getClassLoader());
                Log.e("SS", "MediaPlayer$EventHandler.getClassLoader = " + s.getClassLoader() + "");
                Field mEventHandler = MediaPlayer.class.getDeclaredField("mEventHandler");
                mEventHandler.setAccessible(true);
                Log.d("SS", "" + mEventHandler.get(mp));
                Class<?> SafeEventHandler = Class.forName("android.media.SafeEventHandler", true, s.getClassLoader());
                Constructor constructor = SafeEventHandler.getDeclaredConstructor(MediaPlayer.class, Looper.class);
                constructor.setAccessible(true);
                Object safeEventHandler = constructor.newInstance(mp, looper);
                mEventHandler.set(mp, safeEventHandler);
                Log.d("SS", "SafeMediaPlayer.SafeMediaPlayer: fix ok");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d("SS", "SafeMediaPlayer" + Log.getStackTraceString(e));
        }
    }

}
