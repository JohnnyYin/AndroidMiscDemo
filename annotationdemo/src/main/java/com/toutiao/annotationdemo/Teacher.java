package com.toutiao.annotationdemo;

import android.util.Log;

//老师 类
@GenerateInterface
public class Teacher {

    //教书
    public void teach() {
        Log.d("SS", "Teacher.teach:");
    }

    //行走
    public void walk() {
        Log.d("SS", "Teacher.walk:");
    }
}
