package com.stackviewdemo.stickybroadcastdemo.stickybroadcastdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String SAY_HI_ACTION = "com.stackviewdemo.stickybroadcastdemo.stickybroadcastdemo.MainActivity.say_hi_action";
    private TextView mTextView;
    private Button mBtnSend;
    private Button mBtnRegister;
    private Button mBtnUnregister;
    private Button mBtnRemove;
    private MyReceiver mMyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.txt);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnRemove = (Button) findViewById(R.id.btn_remove);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnUnregister = (Button) findViewById(R.id.btn_unregister);
        mBtnSend.setOnClickListener(this);
        mBtnRemove.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mBtnUnregister.setOnClickListener(this);
        mMyReceiver = new MyReceiver();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registerReceiver();
                break;
            case R.id.btn_send:
                sendStickyBroadcast();
                break;
            case R.id.btn_unregister:
                unregisterReceiver();
                break;
            case R.id.btn_remove:
                removeStickyBroadcast();
                break;
        }
    }

    private void sendStickyBroadcast() {
        Intent intent = new Intent();
        intent.setAction(SAY_HI_ACTION);
        this.sendStickyBroadcast(intent);
    }

    private void removeStickyBroadcast() {
        Intent intent = new Intent();
        intent.setAction(SAY_HI_ACTION);
        this.removeStickyBroadcast(intent);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SAY_HI_ACTION);
        this.registerReceiver(mMyReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(mMyReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && SAY_HI_ACTION.equals(action)) {
                    Toast.makeText(context, "hi~", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
