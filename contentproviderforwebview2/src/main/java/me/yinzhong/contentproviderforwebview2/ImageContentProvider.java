package me.yinzhong.contentproviderforwebview2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("SS", "ImageContentProvider.insert:" + uri);
        if ("/crash".equals(uri.getPath())) {
            Log.d("SS", "ImageContentProvider.insert:crash");
            Process.killProcess(Process.myPid());
            throw new AndroidRuntimeException("test crash.");
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.d("SS", "ImageContentProvider.openFile:" + uri);
        if ("/crash".equals(uri.getPath())) {
            Log.d("SS", "ImageContentProvider.openFile:crash");
            throw new RuntimeException("test crash.");
        }
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        return null;
    }

}
