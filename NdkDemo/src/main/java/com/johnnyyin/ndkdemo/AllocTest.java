package com.johnnyyin.ndkdemo;

public class AllocTest {
    static {
        System.loadLibrary("test");
    }

    public static native String hi();

    public static native void testAllocMemory();

}
