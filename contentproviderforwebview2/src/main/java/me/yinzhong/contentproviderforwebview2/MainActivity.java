package me.yinzhong.contentproviderforwebview2;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.test) {
            try {
                getContentResolver().openFileDescriptor(Uri.parse("content://me.yinzhong.contentproviderforwebview2.ImageContentProvider/crash"), "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.test2) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("key", "value");
            Uri uri = Uri.parse("content://me.yinzhong.contentproviderforwebview2.ImageContentProvider/crash");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ContentProviderClient cpc = null;
                try {
                    cpc = getContentResolver().acquireUnstableContentProviderClient(uri);
                    if (cpc != null) {
                        cpc.insert(uri, contentValues);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d("SS", "MainActivity.onClick:" + Log.getStackTraceString(e));
                } finally {
                    if (cpc != null) {
                        cpc.release();
                    }
                }
            }
        }
    }
}
