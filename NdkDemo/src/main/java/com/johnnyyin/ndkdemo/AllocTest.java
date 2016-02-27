package com.johnnyyin.ndkdemo;

public class AllocTest {
    static {
        System.loadLibrary("test");
    }

    public static native void test();

    public static native void monifyClassLoader(Class<?> a, Class<?> b);
}
