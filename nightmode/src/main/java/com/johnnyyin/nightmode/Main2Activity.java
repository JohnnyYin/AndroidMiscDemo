package com.johnnyyin.nightmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.buton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNightMode = NightModeManager.isNightMode(Main2Activity.this);
                NightModeManager.setNightMode(Main2Activity.this, !isNightMode);
                NightModeManager.updateConfiguration(Main2Activity.this);
                NightModeManager.setLanguage("zh", Main2Activity.this);
                finish();
            }
        });
    }

}
