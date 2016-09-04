package me.yinzhong.ninepatchdrawable;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                if (i++ % 2 == 0) {
                    findViewById(R.id.text).setBackgroundDrawable(null);
                } else {
                    findViewById(R.id.text).setBackgroundDrawable(decodeNinePatchDrawable());
                }
                findViewById(R.id.text).requestLayout();
                findViewById(R.id.text2).setBackgroundDrawable(decodeNinePatchDrawable());
                findViewById(R.id.text2).requestLayout();
                findViewById(R.id.text3).setBackgroundResource(R.drawable.search_background);
            }
        });
    }

    private Drawable decodeNinePatchDrawable() {
        Drawable drawable;
        BitmapFactory.Options op = new BitmapFactory.Options();

        InputStream stream = null;
        Rect padding = new Rect();
        Bitmap bitmap = null;
        try {
            stream = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/test3.9.png");
            bitmap = BitmapFactory.decodeStream(stream, padding, op);
            bitmap.setDensity(DisplayMetrics.DENSITY_XXHIGH);
        } catch (Exception e) {
            /*  do nothing.
                If the exception happened on open, bm will be null.
            */
            Log.e("BitmapFactory", "Unable to decode stream: " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // do nothing here
                }
            }
        }
        byte[] chunk = bitmap.getNinePatchChunk();
        boolean result = NinePatch.isNinePatchChunk(chunk);
        if (result) {
            drawable = new NinePatchDrawable(getResources(), bitmap, chunk, padding, null);
        } else {
            drawable = new BitmapDrawable(bitmap);
        }
        return drawable;
    }
}
