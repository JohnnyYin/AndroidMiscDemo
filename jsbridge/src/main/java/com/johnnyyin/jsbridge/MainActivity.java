package com.johnnyyin.jsbridge;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity implements JSBridge.OnJsMsgListener, View.OnClickListener {
    private WebView mWebView;
    private JSBridge mJsBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.calljs).setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.webview);

        mJsBridge = new JSBridge();
        mJsBridge.setOnJsMsgListener(this);
        mJsBridge.bind(mWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                if (mJsBridge == null || !mJsBridge.checkBridgeSchema(url)) {
                    super.onLoadResource(view, url);
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onMsg(JSBridge.JsMsg jsMsg) {
        if (jsMsg == null) {
            return;
        }
        if ("toast".equals(jsMsg.func)) {
            Toast.makeText(this, jsMsg.params.optString("msg"), Toast.LENGTH_SHORT).show();
            jsMsg.callbackJs(new JSBridge.JsCallback(jsMsg.__callback_id, "Hello, JavaScript.\n--callback from native."));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calljs:
                if (mJsBridge != null) {
                    mJsBridge.callJs("toast", "\"Hello, JavaScript.\\n--send from native.\"");
                }
                break;
        }
    }
}
