
package com.example.graphics_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        final TagAnimView view = (TagAnimView) findViewById(R.id.tag);
        view.setText("文学");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnim();
            }
        });
    }

}
