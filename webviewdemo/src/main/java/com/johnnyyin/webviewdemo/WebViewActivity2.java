package com.johnnyyin.webviewdemo;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebViewActivity2 extends WebViewActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SS", getOriginAssetsPath(getResources().getAssets()).toString());
        dumpDexPath();
    }

    public static List<String> getOriginAssetsPath(AssetManager paramAssetManager) {
        ArrayList localArrayList = new ArrayList();
        try {
            Object localObject = paramAssetManager.getClass().getDeclaredMethod("getStringBlockCount", new Class[0]);
            ((Method) localObject).setAccessible(true);
            int j = ((Integer) ((Method) localObject).invoke(paramAssetManager, new Object[0])).intValue();
            int i = 0;
            for (; ; ) {
                localObject = localArrayList;
                if (i >= j) {
                    break;
                }
                localObject = (String) paramAssetManager.getClass().getMethod("getCookieName", new Class[]{Integer.TYPE}).invoke(paramAssetManager, new Object[]{Integer.valueOf(i + 1)});
                if (!TextUtils.isEmpty((CharSequence) localObject)) {
                    localArrayList.add(localObject);
                }
                i += 1;
            }
            return (List<String>) localObject;
        } catch (Throwable e) {
        }
        return localArrayList;
    }

    public void dumpDexPath() {
        try {
            ClassLoader loader = getClassLoader();
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            Field dexElements = findField(dexPathList, "dexElements");
            Object[] arrays = (Object[]) dexElements.get(dexPathList);
            Log.d("SS", "MainActivity.dumpDexPath:" + Arrays.toString(arrays));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

}
