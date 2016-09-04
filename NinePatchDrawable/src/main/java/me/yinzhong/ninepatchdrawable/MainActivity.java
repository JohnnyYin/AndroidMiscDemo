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
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.text).setBackgroundDrawable(decodeNinePatchDrawable());
            }
        });
    }

    private Drawable decodeNinePatchDrawable() {
        BitmapFactory.Options op = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/test3.9.png", op);
        byte[] chunk = bitmap.getNinePatchChunk();
        boolean result = NinePatch.isNinePatchChunk(chunk);
        Log.d("TR", "MainActivity.decode:ninepatch = " + result);
        if (result) {
            return new NinePatchDrawable(bitmap, chunk, new Rect(), null);
        }
        return new BitmapDrawable(bitmap);
    }
}
