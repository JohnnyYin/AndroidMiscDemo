package com.johnnyyin.jsbridge;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity implements JSBridge.OnJsMsgListener {
    private WebView mWebView;
    private JSBridge mJsBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJsBridge = new JSBridge();
        mJsBridge.setOnJsMsgListener(this);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                mJsBridge.checkBridgeSchema(url, mWebView);
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
        if ("hello".equals(jsMsg.func)) {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
    }
}
