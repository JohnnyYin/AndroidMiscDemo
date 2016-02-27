package com.johnnyyin.themedemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ThemeUtils {
    public static int THEME = R.style.AppTheme;

    public static int getWindowBackground(Activity activity) {
        int backgroundResId = 0;
        if (activity == null) {
            return backgroundResId;
        }
        TypedArray a = activity.obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        if (a != null) {
            backgroundResId = a.getResourceId(0, 0);
            a.recycle();
        }
        return backgroundResId;
    }

    public static void clearAbsListViewRecycleBin(AbsListView listView) {
        if (listView == null) {
            return;
        }
        try {
            Field mRecycler = AbsListView.class.getDeclaredField("mRecycler");
            mRecycler.setAccessible(true);
            Method localMethod = Class.forName("android.widget.AbsListView$RecycleBin").getDeclaredMethod("clear");
            localMethod.setAccessible(true);
            localMethod.invoke(mRecycler.get(listView));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static ColorStateList getTextColor(Activity activity) {
        ColorStateList colorStateList = null;

        if (activity == null) {
            return colorStateList;
        }
        TypedArray a = activity.obtainStyledAttributes(null, new int[]{android.R.attr.textAppearance}, android.R.attr.textViewStyle, 0);
        TypedArray appearance = null;
        int ap = a.getResourceId(0, -1);
        a.recycle();
        if (ap > 0) {
            appearance = activity.obtainStyledAttributes(ap, new int[]{android.R.attr.textColor});
        }
        if (appearance != null) {
            colorStateList = a.getColorStateList(0);
            appearance.recycle();
        }
        a = activity.obtainStyledAttributes(null, new int[]{android.R.attr.textColor}, android.R.attr.textViewStyle, 0);
        ColorStateList tempId = a.getColorStateList(0);
        if (tempId != null) {
            colorStateList = tempId;
        }
        a.recycle();
        return colorStateList;
    }

    public static Drawable getListSelector(Activity activity) {
        if (activity == null) {
            return null;
        }
        Drawable d = null;

        final TypedArray a = activity.obtainStyledAttributes(null, new int[]{android.R.attr.listSelector}, android.R.attr.listViewStyle, 0);
        if (a != null) {
            d = a.getDrawable(0);
            a.recycle();
        }
        return d;
    }

    public static Drawable getBaseClickableBackground(Activity activity) {
        if (activity == null) {
            return null;
        }

        Drawable d = null;
        TypedArray a = activity.obtainStyledAttributes(R.style.BaseClickable, new int[]{android.R.attr.background});
        if (a != null) {
            d = a.getDrawable(0);
            dump(a, 0);
            a.recycle();
        }
        return d;
    }

    public static Drawable getBaseClickableOnlyRippleEffectBackground(Context context) {
        if (context == null) {
            return null;
        }

        Drawable d = null;
        TypedArray a = context.obtainStyledAttributes(R.style.BaseClickable_OnlyRippleEffect, new int[]{android.R.attr.background});
        if (a != null) {
            dump(a, 0);
            d = a.getDrawable(0);
            dump(a, 0);
            a.recycle();
        }
        return d;
    }

    private static void dump(TypedArray a, int index) {
        int id = a.getResourceId(index, -1);
        String name = "";
        if (id > 0) {
            name = a.getString(index);
        }
        Log.e("SS", id + ", " + name);
    }

    public static Drawable getButtonBackground(Activity activity) {
        return getBackground(activity, android.R.attr.buttonStyle);
    }

    public static Drawable getBackground(Activity activity, int defStyleAttr) {
        if (activity == null) {
            return null;
        }
        Drawable d = null;

        final TypedArray a = activity.obtainStyledAttributes(null, new int[]{android.R.attr.background}, defStyleAttr, 0);
        if (a != null) {
            d = a.getDrawable(0);
            a.recycle();
        }
        return d;
    }

    public static int setStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return -1;
        }
        if (activity == null) {
            return -1;
        }
        int c = -1;

        final TypedArray a = activity.obtainStyledAttributes(new int[]{android.R.attr.statusBarColor});
        if (a != null) {
            c = a.getColor(0, -1);
            if (c != -1) {
                activity.getWindow().setStatusBarColor(c);
            }
            a.recycle();
        }
        return c;
    }

}
