package com.johnnyyin.getobjectsize;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MainActivity extends Activity {
    private static final byte[] bs = new byte[1024 * 2];

    static {
        Arrays.fill(bs, (byte) 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("SS", "MainActivity.class size = " + getObjectSize(MainActivity.class) + " " + getClassSize(MainActivity.class));
    }

    private int getObjectSize(Class<?> c) {
        try {
            Field f = Class.class.getDeclaredField("objectSize");
            f.setAccessible(true);
            int value = f.getInt(c);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SS", "Exception = " + Log.getStackTraceString(e));
        }
        return 0;
    }


    private int getClassSize(Class<?> c) {
        try {
            Field f = Class.class.getDeclaredField("classSize");
            f.setAccessible(true);
            int value = f.getInt(c);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SS", "Exception = " + Log.getStackTraceString(e));
        }
        return 0;
    }

}
