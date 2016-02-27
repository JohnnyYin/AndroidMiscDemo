package com.test.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class HotFix {
    public static void main(String[] args) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.handleMsg();
        } catch (Exception e) {
            try {
                try {
                    Class.forName("com.test.reflect.MediaPlayer.MyHandler");
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
                Class c = Class.forName("com.test.reflect.FixedHandler");
                Constructor constructor = c
                        .getDeclaredConstructor(MediaPlayer.class);
                Object fixedHandler = constructor.newInstance(mediaPlayer);
                Field field = MediaPlayer.class.getDeclaredField("mHandler");
                field.setAccessible(true);
                field.set(mediaPlayer, fixedHandler);
                mediaPlayer.handleMsg();
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
