package me.yinzhong.activitybackgrounddemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

/**
 * 只是将Activity的背景设置为透明，不修改theme中的"android:windowIsTranslucent"属性
 *
 * @author YinZhong
 * @since 2016/11/13
 */
public class TransparentBackgroundActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.format = PixelFormat.TRANSLUCENT;
        getWindow().setAttributes(lp);
        super.onCreate(savedInstanceState);
    }
}
