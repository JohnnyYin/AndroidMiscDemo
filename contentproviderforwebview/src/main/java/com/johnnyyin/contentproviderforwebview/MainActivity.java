package com.johnnyyin.contentproviderforwebview;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity implements ImageContentProvider.ImageLoadListener {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageContentProvider.addListener(this);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request != null && request.getUrl().getScheme().startsWith("img")) {
                    Uri uri = request.getUrl();
                    String path = uri.getHost();
                    try {
                        InputStream inputStream = new FileInputStream(new File(ImageContentProvider.DCIM, path));
                        WebResourceResponse response = new WebResourceResponse("image/jpg", "utf-8", inputStream);
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageContentProvider.removeListener(this);
    }

    @Override
    public void onLoaded(final int index, final boolean ok) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(String.format("javascript:on_image_loaded(%s, %s)", index, ok));
            }
        });
    }
}
