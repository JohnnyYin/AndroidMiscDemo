package com.johnnyyin.webviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "SS";
    private RecyclerView mRecyclerView;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mData = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            mData.add("item:" + i);
        }
        mRecyclerView.setAdapter(new CustomAdapter());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }

    private class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int ITEM_OTHER = 0;
        private static final int ITEM_WEBVIEW = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView;
            if (viewType == ITEM_WEBVIEW) {
                itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.webview, viewGroup, false);
                return new WebViewHolder(itemView);
            } else {
                itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.text_row_item, viewGroup, false);
                return new ListViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewType = viewHolder.getItemViewType();
            if (viewType == ITEM_OTHER) {
                ((ListViewHolder) viewHolder).mContent.setText(mData.get(position));
            } else if (viewType == ITEM_WEBVIEW) {
                ((WebViewHolder) viewHolder).onBind();
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? ITEM_WEBVIEW : ITEM_OTHER;
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent;

        public ListViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.content);
        }

    }

    private class WebViewHolder extends RecyclerView.ViewHolder {
        private WebView mWebView;
        private static final String URL = "http://ic.snssdk.com/discover/wap/discover_page/?iid=3097211666&device_id=3031864920&ac=wifi&channel=local_test&aid=13&app_name=news_article&version_code=497&version_name=4.9.7&device_platform=android&device_type=Nexus+5&os_api=22&os_version=5.1.1&uuid=358239053212017&openudid=ac192f7439884033&manifest_version_code=497";

        public WebViewHolder(View itemView) {
            super(itemView);
            mWebView = (WebView) itemView;
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.e(TAG, "WebViewDemo-shouldOverrideUrlLoading");
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
        }

        public void onBind() {
            if (!URL.equalsIgnoreCase(mWebView.getOriginalUrl())) {
                mWebView.loadUrl(URL);
            }
        }

    }

}
