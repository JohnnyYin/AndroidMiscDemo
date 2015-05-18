package com.stackviewdemo.porterduffdemo.porterduffdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity implements View.OnClickListener {
    private Button mBtn;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) findViewById(R.id.btn);
        mImg = (ImageView) findViewById(R.id.img);
        mBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 覆盖一层颜色
     *
     * @param drawable
     * @param color
     */
    public static void coverColor(Drawable drawable, int color) {
        if (drawable != null) {
            drawable.clearColorFilter();
            drawable.mutate();
            // LightingColorFilter c = new LightingColorFilter(mul, add);
            // ColorMatrix cm = new ColorMatrix();
            // cm.setSaturation(0);
            // ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
            drawable.setColorFilter(color, PorterDuff.Mode.LIGHTEN);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                changeImage();
                break;
        }
    }

    private void changeImage() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        // coverColor(drawable, 0x44FFFFFF);
        //mImg.setImageDrawable(drawable);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        bitmap = toGrayscale(bitmap);
        mImg.setImageBitmap(bitmap);
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
