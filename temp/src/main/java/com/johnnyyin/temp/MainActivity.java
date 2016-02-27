package com.johnnyyin.temp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.Test;

import java.io.File;

public class MainActivity extends Activity {
    private ListView mListView;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                    animation.setDuration(300);
                    animation.setFillAfter(true);
                    animation.setFillEnabled(true);
                    v.startAnimation(animation);
                    flag = true;
                } else {
                    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    animation.setDuration(300);
                    animation.setFillAfter(true);
                    animation.setFillEnabled(true);
                    v.startAnimation(animation);
                    flag = false;
                }
            }
        });
        findViewById(R.id.text).offsetTopAndBottom(300);

        Log.d("SS", "MainActivity.onCreate:" + Test.test());
        Log.e("SS", getResources().getResourceEntryName(R.layout.activity_main) + ":" + getResources().getIdentifier("activity_main", "layout", getPackageName()));

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 300;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.text)).setText("item: " + position);
                return convertView;
            }
        });

        Log.e("SS", "Activity.getApplication = " + getApplication());
        Log.e("SS", "Activity.getApplicationContext = " + getApplicationContext());
        Log.e("SS", "Application.this = " + MyApplication.sInstance);
        Log.e("SS", "Application.getApplicationContext = " + MyApplication.sInstance.getApplicationContext());

        Log.e("SS", "Activity.getBaseContext = " + getBaseContext());
        Log.e("SS", "Application.getBaseContext = " + MyApplication.sInstance.getBaseContext());

        Log.e("SS", "Activity = " + this);
        Log.e("SS", "mListView.getContext = " + mListView.getContext());
        Log.e("SS", "Window.getContext = " + getWindow().getContext());
        Log.e("SS", "DecorView.getContext = " + getWindow().getDecorView().getContext());

        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_main, null, false);
        Log.e("SS", "view.getContext = " + view.getContext());

        Log.e("SS", "sign = " + DigestUtils.md5Hex(SignUtils.getApkSignature("/sdcard/base.apk")));
//        apkInfo("/data/app/com.johnnyyin.temp-1/base.apk", this);
        Log.e("SS", "sign = " + getSigHash(this));
        File file = new File("/data/app/com.johnnyyin.temp-1/base.apk");
        Log.e("SS", "" + file.exists() + ", " + file.canRead());
        Main.parse("/data/app/com.johnnyyin.temp-1/base.apk");

        new Thread() {
            @Override
            public void run() {
                super.run();
                com.johnnyyin.temp.Test.main(null);
            }
        }.start();
    }

    public static String getSigHash(Context context) {
        String sinHash = null;
        if (context != null) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageInfo(context.getPackageName(),
                        PackageManager.GET_SIGNATURES);
                Signature sig = info.signatures[0];
                byte[] data = sig.toByteArray();
                sinHash = DigestUtils.md5Hex(data);
            } catch (Exception e) {
            }
        }
        return sinHash;
    }

    public void apkInfo(String absPath, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_SIGNATURES);
        Log.e("SS", DigestUtils.md5Hex(pkgInfo.signatures[0].toByteArray()));
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String packageName = appInfo.packageName; // 得到包名
            String version = pkgInfo.versionName; // 得到版本信息
        /* icon1和icon2其实是一样的 */
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
