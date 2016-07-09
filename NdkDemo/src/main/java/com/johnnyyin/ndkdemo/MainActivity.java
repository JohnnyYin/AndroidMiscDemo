package com.johnnyyin.ndkdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllocTest.testAllocMemory();
            }
        });
        getSign(this);
    }

    private void getSign(Context context) {
        String signature_key = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            if (signatures != null && signatures.length > 0) {
                Log.d("TR", "MainActivity.getSign:" + signatures[0].toCharsString());
                MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
                localMessageDigest.update(signatures[0].toByteArray());
                // 这个就是签名的md5值
                String str2 = toHex(localMessageDigest.digest());
                Log.d("TR", "MainActivity.getSign:" + str2);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String toHex(byte[] paramArrayOfByte) {
        StringBuilder localStringBuffer = new StringBuilder();
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = paramArrayOfByte[i];
            localStringBuffer.append(String.format("%02x", arrayOfObject));
        }
        return localStringBuffer.toString();
    }
}
