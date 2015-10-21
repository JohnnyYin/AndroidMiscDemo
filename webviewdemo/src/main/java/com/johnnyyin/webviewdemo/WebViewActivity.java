package com.johnnyyin.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebViewActivity extends Activity {
    public static final String DISCOVER_URL = "http://ic.snssdk.com/discover/wap/discover_page/?iid=3097211666&device_id=3031864920&ac=wifi&channel=local_test&aid=13&app_name=news_article&version_code=497&version_name=4.9.7&device_platform=android&device_type=Nexus+5&os_api=22&os_version=5.1.1&uuid=358239053212017&openudid=ac192f7439884033&manifest_version_code=497";
    public static final String SEARCH_RESULT_URL = "http://ic.snssdk.com/api/2/wap/search/?from=tag&keyword=%E8%87%AA%E9%A9%BE%E6%B8%B8&iid=3097211666&device_id=3031864920&ac=wifi&channel=local_test&aid=13&app_name=news_article&version_code=497&version_name=4.9.7&device_platform=android&device_type=Nexus+5&os_api=22&os_version=5.1.1&uuid=358239053212017&openudid=ac192f7439884033&manifest_version_code=497&search_sug=1&forum=1&latitude=39.9737379&longitude=116.3326189";

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
                Intent intent1 = new Intent(WebViewActivity.this, WebViewActivity.class);
                intent1.putExtra("url", DISCOVER_URL);
                startActivity(intent1);
            }
        });
        WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(url);
    }

}
