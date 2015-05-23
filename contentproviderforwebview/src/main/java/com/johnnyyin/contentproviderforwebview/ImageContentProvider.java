package com.johnnyyin.contentproviderforwebview;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Johnny on 15/5/23.
 */
public class ImageContentProvider extends ContentProvider {

    public interface ImageLoadListener {
        void onLoaded(int index, boolean ok);
    }

    public static final String DCIM = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM;
    private static final String ACTION_LOAD_IMG = "loadimg";
    private static final String ACTION_SHOW_IMG = "showimg";

    private static Set<ImageLoadListener> sImageLoadListeners = new HashSet<ImageLoadListener>();

    public static void addListener(ImageLoadListener imageLoadListener) {
        if (imageLoadListener != null)
            sImageLoadListeners.add(imageLoadListener);
    }

    public static void removeListener(ImageLoadListener imageLoadListener) {
        if (imageLoadListener != null)
            sImageLoadListeners.remove(imageLoadListener);
    }

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
        if (uri != null) {
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments == null || pathSegments.isEmpty()) {
                return null;
            }
            String action = pathSegments.get(0);
            String path = uri.getQueryParameter("path");
            if (ACTION_LOAD_IMG.equals(action)) {
                int index = -1;
                try {
                    index = Integer.parseInt(uri.getQueryParameter("index"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (index < 0) {
                    return null;
                }
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(DCIM, path);
                    boolean ok = file.exists() && file.isFile() && file.canRead();
                    for (ImageLoadListener imageLoadListener : sImageLoadListeners) {
                        imageLoadListener.onLoaded(index, ok);
                    }
                }
            } else if (ACTION_SHOW_IMG.equals(action)) {
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(DCIM, path);
                    if (file.exists() && file.isFile() && file.canRead()) {
                        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    }
                }
            }
        }
        return null;
    }

}
