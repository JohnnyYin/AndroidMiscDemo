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
    private static final int WHAT_JS_MSG_CALL = 1;
    private static final int WHAT_JS_MSG_CALLBACK = 2;
    private static final String CUSTOM_PROTOCOL_SCHEME = "weixin";
    private static final String DISPATCH_MESSAGE = CUSTOM_PROTOCOL_SCHEME + "://dispatch_message/";
    private static final String SET_RESULT = CUSTOM_PROTOCOL_SCHEME + "://private/setresult/";

    public static final class JsMsg {
        public static final String MSG_TYPE_CALL = "call";
        public static final String MSG_TYPE_CALLBACK = "callback";
        public static final String MSG_TYPE_EVENT = "event";
        String func;
        JSONObject params;
        String __callback_id;
        String __msg_type;
        JSBridge bridge;

        public static JsMsg parse(JSBridge bridge, JSONObject o) {
            if (o == null) {
                return null;
            }
            JsMsg jsMsg = new JsMsg();
            jsMsg.func = o.optString("func");
            jsMsg.params = o.optJSONObject("params");
            jsMsg.__callback_id = o.optString("__callback_id");
            jsMsg.__msg_type = o.optString("__msg_type");
            jsMsg.bridge = bridge;
            return jsMsg.valid() ? jsMsg : null;
        }

        public boolean valid() {
            return !TextUtils.isEmpty(func) && !TextUtils.isEmpty(__msg_type);
        }

        public void callbackJs(JsCallback jsCallback) {
            if (bridge != null) {
                bridge.scheduleCallbackJs(jsCallback);
            }
        }

        @Override
        public String toString() {
            return "JsMsg{" +
                    "func='" + func + '\'' +
                    ", params=" + params +
                    ", __callback_id='" + __callback_id + '\'' +
                    ", __msg_type='" + __msg_type + '\'' +
                    '}';
        }
    }

    public static final class JsCallback {
        String __callback_id;
        String __params;
        String __msg_type;

        public JsCallback(String __callback_id, String __params) {
            this.__callback_id = __callback_id;
            this.__params = __params;
            this.__msg_type = JsMsg.MSG_TYPE_CALLBACK;
        }

        public String toJson() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("__callback_id", __callback_id);
                jsonObject.put("__msg_type", __msg_type);
                jsonObject.put("__params", __params);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface OnJsMsgListener {
        void onMsg(JsMsg jsMsg);
    }

    private OnJsMsgListener mOnJsMsgListener;
    private WebView mWebView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_JS_MSG_CALL:
                    if (msg.obj instanceof JsMsg && mOnJsMsgListener != null) {
                        mOnJsMsgListener.onMsg((JsMsg) msg.obj);
                    }
                    break;
                case WHAT_JS_MSG_CALLBACK:
                    if (msg.obj instanceof JsCallback) {
                        callbackJs((JsCallback) msg.obj);
                    }
                    break;
            }
        }
    };

    public boolean checkBridgeSchema(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (DISPATCH_MESSAGE.equals(url)) {
            callJs("WeixinJSBridge._fetchQueue", null);
        } else if (url.startsWith(SET_RESULT)) {
            int start = SET_RESULT.length();
            int index = url.indexOf('&', start);
            if (index <= 0) {
                return false;
            }
            String scene = url.substring(start, index);
            String msg = url.substring(index + 1);
            if ("SCENE_FETCHQUEUE".equals(scene) && msg.length() > 0) {
                return parseMsgQueue(msg);
            }
        }
        return false;
    }

    private boolean parseMsgQueue(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        final String s = new String(Base64.decode(msg, Base64.NO_WRAP));
        try {
            JSONArray a = new JSONArray(s);
            final int len = a.length();
            for (int i = 0; i < len; i++) {
                JSONObject o = a.getJSONObject(i);
                JsMsg jsMsg = JsMsg.parse(this, o);
                if (jsMsg == null)
                    continue;
                if (JsMsg.MSG_TYPE_CALL.equals(jsMsg.__msg_type)) {
                    mHandler.obtainMessage(WHAT_JS_MSG_CALL, jsMsg).sendToTarget();
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setOnJsMsgListener(OnJsMsgListener onJsMsgListener) {
        this.mOnJsMsgListener = onJsMsgListener;
    }

    public void bind(WebView webView) {
        this.mWebView = webView;
    }

    public void callJs(String func, String params) {
        if (params == null) {
            params = "";
        }
        if (checkWebViewStatus()) {
            mWebView.loadUrl("javascript:" + func + "(" +
                    params + ")");
        }
    }

    public void scheduleCallbackJs(JsCallback jsCallback) {
        if (jsCallback == null && !checkWebViewStatus())
            return;
        mHandler.obtainMessage(WHAT_JS_MSG_CALLBACK, jsCallback).sendToTarget();
    }

    private void callbackJs(JsCallback jsCallback) {
        if (checkWebViewStatus() && jsCallback != null) {
            String json = jsCallback.toJson();
            if (!TextUtils.isEmpty(json)) {
                callJs("WeixinJSBridge._handleMessageFromWeixin", json);
            }
        }
    }

    private boolean checkWebViewStatus() {
        return mWebView != null;
    }

}
