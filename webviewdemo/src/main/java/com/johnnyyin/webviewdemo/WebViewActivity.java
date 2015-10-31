package com.johnnyyin.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends Activity {
    public static final String DISCOVER_URL = "http://ic.snssdk.com/discover/wap/discover_page/?iid=3097211666&device_id=3031864920&ac=wifi&channel=local_test&aid=13&app_name=news_article&version_code=497&version_name=4.9.7&device_platform=android&device_type=Nexus+5&os_api=22&os_version=5.1.1&uuid=358239053212017&openudid=ac192f7439884033&manifest_version_code=497";
    public static final String SEARCH_RESULT_URL = "http://ic.snssdk.com/api/2/wap/search/?from=tag&keyword=%E8%87%AA%E9%A9%BE%E6%B8%B8&iid=3097211666&device_id=3031864920&ac=wifi&channel=local_test&aid=13&app_name=news_article&version_code=497&version_name=4.9.7&device_platform=android&device_type=Nexus+5&os_api=22&os_version=5.1.1&uuid=358239053212017&openudid=ac192f7439884033&manifest_version_code=497&search_sug=1&forum=1&latitude=39.9737379&longitude=116.3326189";

    private WebView mWebView;
    private String mUserAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            url = DISCOVER_URL;
        }
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent1 = new Intent(WebViewActivity.this, WebViewActivity.class);
//                intent1.putExtra("url", DISCOVER_URL);
//                startActivity(intent1);
                mWebView.getSettings().setUserAgentString(mUserAgent);
                mWebView.loadUrl("javascript:alert(navigator.userAgent)");
                mWebView.loadUrl("javascript:alert(window.location.href)");
                Log.e("SS", mWebView.getUrl());

            }
        });
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        StringBuilder sb = new StringBuilder(settings.getUserAgentString());
        sb.append(" NewsArticle/5.0.0");
        sb.append("  NetType/wifi");
        mUserAgent = sb.toString();
        settings.setUserAgentString(mUserAgent);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("SS", "shouldOverrideUrlLoading" + url);
                if (url.startsWith("https://m.baidu.com")) {
                    url = url.replace("http://www.baidu.com", "http://photo.sina.cn/");
                    view.loadUrl("http://photo.sina.cn/");
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.getSettings().setUserAgentString(mUserAgent);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("SSSS", "errorCode = " + errorCode);
//                mWebView.loadUrl("file:///android_asset/error.html");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("SSSS", "onLoadResource = " + url);
                super.onLoadResource(view, url);
            }
        });
//        webView.loadUrl("http://errrrrrr.com");
//        webView.loadUrl("http://10.2.21.126/local_gallery/demo.html");
//        mWebView.loadUrl("http://www.baidu.com");
//        mWebView.loadUrl("http://slide.news.sina.com.cn/x/slide_1_64237_90207.html#p=1");
//        mWebView.loadUrl("http://photo.sina.cn/album_4_704_122666.htm?ch=4&wm=3164_0005&vt=4");
//        mWebView.loadUrl("http://local");
        Map<String, String> map = new HashMap<>();
        mWebView.loadUrl("http://photo.sina.cn", map);
//        mWebView.loadUrl("file:///android_asset/normal.html");
    }

}