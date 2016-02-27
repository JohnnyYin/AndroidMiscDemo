package com.johnnyyin.floatwindowdemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class FloatDialog extends WindowBuilder {
    private WindowBase mWindowBase;

    public FloatDialog(Activity context) {
        super(context);
        init();
    }
    public FloatDialog() {
        super((View) null);
        init();
    }

    @Override
    public ViewGroup getRootView() {
        return (ViewGroup) LayoutInflater.from(BaseApplication.getInst()).inflate(R.layout.video_controller_layout, null);
    }

    @Override
    public WindowBase getWindowBase() {
        if (mWindowBase == null) {
            mWindowBase = new WindowBase() {
                @Override
                public LayoutParams initLayoutParams() {
                    LayoutParams lp = new LayoutParams();
                    lp.width = LayoutParams.WRAP_CONTENT;
                    lp.height = LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.LEFT | Gravity.TOP;
                    lp.format = PixelFormat.RGBA_8888;
                    lp.type = LayoutParams.TYPE_APPLICATION_PANEL;
//                    lp.type = LayoutParams.TYPE_TOAST;
                    lp.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM
                            | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                    lp.packageName = BaseApplication.getInst().getPackageName();
                    return lp;
                }
            };
        }
        return mWindowBase;
    }

    private void init() {
        findId(R.id.txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "hahahahah", Toast.LENGTH_SHORT).show();
                Log.e("SS", "onClick");
            }
        });
    }

    public void show(int width, int height) {
        mWindowBase.getLayoutParams().width = width;
        mWindowBase.getLayoutParams().height = height;
        super.show();
    }
}
