package com.johnnyyin.jsbridge;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSBridge {
    private static final int WHAT_JS_MSG = 1;

    public static final class JsMsg {
        String func;
    }

    public interface OnJsMsgListener {
        void onMsg(JsMsg jsMsg);
    }

    private OnJsMsgListener mOnJsMsgListener;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_JS_MSG:
                    if (msg.obj instanceof JsMsg && mOnJsMsgListener != null) {
                        mOnJsMsgListener.onMsg((JsMsg) msg.obj);
                        break;
                    }
            }
        }
    };

    public void onJsMsg(String msg) {

    }

    public void checkBridgeSchema(String url, WebView webView) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String p_result = "weixin://private/setresult/";
        if ("weixin://dispatch_message/".equals(url)) {
            webView.loadUrl("javascript:WeixinJSBridge._fetchQueue()");
        } else if (url.startsWith("weixin://private/setresult/")) {
            int start = p_result.length();
            int index = url.indexOf('&', start);
            if (index <= 0) {
                return;
            }
            String scene = url.substring(start, index);
            String msg = url.substring(index + 1);
            if (scene.equals("SCENE_FETCHQUEUE") && msg.length() > 0) {
                parseMsgQueue(msg);
            }
        }
    }

    private void parseMsgQueue(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        final String s = new String(Base64.decode(msg, Base64.NO_WRAP));
        try {
            JSONArray a = new JSONArray(s);
            final int len = a.length();
            for (int i = 0; i < len; i++) {
                JSONObject o = a.getJSONObject(i);
                JsMsg jsMsg = new JsMsg();
                jsMsg.func = o.optString("func");
                mHandler.obtainMessage(WHAT_JS_MSG, jsMsg).sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOnJsMsgListener(OnJsMsgListener onJsMsgListener) {
        this.mOnJsMsgListener = onJsMsgListener;
    }

}
