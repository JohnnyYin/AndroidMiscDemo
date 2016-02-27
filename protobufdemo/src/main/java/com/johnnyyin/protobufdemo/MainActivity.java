package com.johnnyyin.protobufdemo;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.wire.Wire;
import com.ss.android.kvobj.KV;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SS";
    private Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonOuterClass.Person person = PersonOuterClass.Person.newBuilder()
                        .setEmail("729271608@qq.com")
                        .setId(1)
                        .setName("Johnny").build();
                Log.d(TAG, "MainActivity.person = " + person);
                ByteString byteString = person.toByteString();
                Log.d(TAG, "MainActivity.byteString = :" + byteString);
                try {
                    PersonOuterClass.Person p2 = PersonOuterClass.Person.parseFrom(byteString);
                    Log.d(TAG, "MainActivity.p2 = " + p2);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.test_wire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person1 = new Person.Builder().id(1).name("name").email("729271608@qq.com").build();
                Log.e(TAG, "wire test : " + person1);
                try {
                    byte[] bytes = person1.toByteArray();
                    int num = 1;

                    long pbTime = 0;
                    long wireTime = 0;
                    long gsonTime = 0;

                    long startTime = SystemClock.uptimeMillis();
                    for (int i = 0; i < num; i++) {
                        Person person2 = Person.parseFrom(bytes, Person.class);
                        Log.e(TAG, "p = " + person2);
                    }
                    Log.e(TAG, "wire parse time : " + (wireTime = (SystemClock.uptimeMillis() - startTime)));

                    startTime = SystemClock.uptimeMillis();
                    for (int i = 0; i < num; i++) {
                        PersonOuterClass.Person p2 = PersonOuterClass.Person.parseFrom(bytes);
                    }
                    Log.e(TAG, "PB parse time : " + (pbTime = (SystemClock.uptimeMillis() - startTime)));

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", "Johnny");
                    jsonObject.put("id", 1);
                    jsonObject.put("email", "729271608@qq.com");
                    String jStr = jsonObject.toString();

                    startTime = SystemClock.uptimeMillis();
                    for (int i = 0; i < num; i++) {
                        Person s = Person.parseFrom(jStr, Person.class);
                        Log.e(TAG, "p2 = " + s);
                    }
                    Log.e(TAG, "Gson parse time : " + (gsonTime = (SystemClock.uptimeMillis() - startTime)));

                    Log.e("AA", "totalTime : " + wireTime + "\t" + pbTime + "\t" + gsonTime);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGson();
            }
        });

        findViewById(R.id.compare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compare();
            }
        });
        findViewById(R.id.compare2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compare2();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void testGson() {
        mGson = new GsonBuilder()
                .registerTypeAdapterFactory(new BundleTypeAdapterFactory())
                .create();
        try {
            JSONObject source = new JSONObject("{\"str\":\"ssss\",\"b\":1,\"c\":\'&\',\"s\":2,\"i\":4,\"l\":8,\"f\":16.23,\"d\":32.34,\"bool\":true," +
                    "\"bo\":{\"s\":\"this is B instance\"}," +
                    "\"byteArray\":true,\"list\":[[{\"s\":\"this is list\"}]]," +
                    "\"set\":[[{\"s\":\"this is set\"}]]," +
                    "\"queue\":[[{\"s\":\"this is queue\"}]]," +
                    "\"array\":[{\"s\":\"this is array\"}]," +
                    "\"map\":{\"xxx\":{\"yyy\":{\"s\":\"this is map\"}}}," +
                    "\"sArray\":{\"1\":{\"1\":{\"s\":\"this is sparseArray\"}}}," +
                    "\"jsonObject\":{\"name\":\"john\"}," +
                    "\"jsonArray\":[{\"name\":\"john\"},{\"name\":\"rose\"}]," +
                    "\"host\":{\"hostStr\":\"this is Host String.\",\"hostB\":{\"s\":\"this is Host B\"}}}");
            String sourceStr = source.toString();
            // map的转换
            HashMap<String, Object> map = (HashMap<String, Object>) mGson.fromJson(sourceStr, new HashMap<String, Object>().getClass());

            for (String key : map.keySet()) {
                Log.d(TAG, "Gson test Map :key = " + key + ", value = " + map.get(key));
            }
            Log.d(TAG, "Gson test Map to Json :" + mGson.toJson(map));

            Bundle bundle = mGson.fromJson(sourceStr, Bundle.class);

            for (String key : bundle.keySet()) {
                Log.d(TAG, "Gson test Bundle :key = " + key + ", value = " + map.get(key));
            }
            Log.d(TAG, "Gson test Bundle to Json :" + mGson.toJson(bundle));
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d(TAG, "Main2Activity.testGson:" + Log.getStackTraceString(e));
        }
    }

    private void compare() {
        try {
            JSONObject source;
            A1 result = null;
            String baseStr = "HelloWorld, 你好，世界";
            String base64TestStr = Base64.encodeToString(baseStr.getBytes(), Base64.NO_WRAP);
            source = new JSONObject("{\"str\":\"ssss\",\"b\":1,\"c\":\'&\',\"s\":2,\"i\":4,\"l\":8,\"f\":16.23,\"d\":32.34,\"bool\":true," +
                    "\"bo\":{\"s\":\"this is B instance\"}," +
                    "\"byteArray\":true,\"list\":[[{\"s\":\"this is list\"}]]," +
                    "\"set\":[[{\"s\":\"this is set\"}]]," +
                    "\"queue\":[[{\"s\":\"this is queue\"}]]," +
                    "\"array\":[{\"s\":\"this is array\"}]," +
                    "\"map\":{\"xxx\":{\"yyy\":{\"s\":\"this is map\"}}}," +
                    "\"sArray\":{\"1\":{\"1\":{\"s\":\"this is sparseArray\"}}}," +
                    "\"jsonObject\":{\"name\":\"john\"}," +
                    "\"jsonArray\":[{\"name\":\"john\"},{\"name\":\"rose\"}]," +
                    "\"host\":{\"hostStr\":\"this is Host String.\",\"hostB\":{\"s\":\"this is Host B\"}}}");

            String str = source.toString();

            final int num = 100;
            A1 a;
            long fastJsonTime = 0;
            long gsonTime = 0;
            long kvTime = 0;
            long originTime = 0;
            long start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                a = JSON.parseObject(str, A1.class);
            }
            fastJsonTime = (SystemClock.uptimeMillis() - start);
//            Log.e(TAG, "FastJson time = " + fastJsonTime);

            start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                a = KV.fromJson(str, A1.class);
            }
            kvTime = (SystemClock.uptimeMillis() - start);
//            Log.e(TAG, "KV time = " + gsonTime);

//            ObjectMapper objectMapper = new ObjectMapper();
//
//            start = SystemClock.uptimeMillis();
//            for (int i = 0; i < 20; i++) {
//                a = objectMapper.readValue(str, A1.class);
//            }
//            Log.e(TAG, "Jsckson time = " + (SystemClock.uptimeMillis() - start));

            start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                a = mGson.fromJson(str, A1.class);
            }
            gsonTime = (SystemClock.uptimeMillis() - start);
//            Log.e(TAG, "Gson time = " + gsonTime);

            start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                a = new A1(str);
            }
            originTime = (SystemClock.uptimeMillis() - start);
//            Log.e(TAG, "Origin time = " + originTime);
            Log.e(TAG, "" + fastJsonTime + "\t" + kvTime + "\t" + gsonTime + "\t" + originTime);
        } catch (Throwable e) {
            Log.e(TAG, "" + Log.getStackTraceString(e));
        }
    }

    private void compare2() {
        try {
            JSONObject source;
            A1 result = null;
            String baseStr = "HelloWorld, 你好，世界";
            String base64TestStr = Base64.encodeToString(baseStr.getBytes(), Base64.NO_WRAP);
            source = new JSONObject("{\"str\":\"ssss\",\"b\":1,\"c\":\'&\',\"s\":2,\"i\":4,\"l\":8,\"f\":16.23,\"d\":32.34,\"bool\":true," +
                    "\"bo\":{\"s\":\"this is B instance\"}," +
                    "\"byteArray\":true,\"list\":[[{\"s\":\"this is list\"}]]," +
                    "\"set\":[[{\"s\":\"this is set\"}]]," +
                    "\"queue\":[[{\"s\":\"this is queue\"}]]," +
                    "\"array\":[{\"s\":\"this is array\"}]," +
                    "\"map\":{\"xxx\":{\"yyy\":{\"s\":\"this is map\"}}}," +
                    "\"sArray\":{\"1\":{\"1\":{\"s\":\"this is sparseArray\"}}}," +
                    "\"jsonObject\":{\"name\":\"john\"}," +
                    "\"jsonArray\":[{\"name\":\"john\"},{\"name\":\"rose\"}]," +
                    "\"host\":{\"hostStr\":\"this is Host String.\",\"hostB\":{\"s\":\"this is Host B\"}}}");

            String str = source.toString();

            final int num = 1000;
            A1 a = new A1(str);

            long fastJsonTime = 0;
            long gsonTime = 0;
            long originTime = 0;
            long start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                String json = JSON.toJSONString(a);
                Log.e(TAG, "fast = " + json);
            }
            fastJsonTime = (SystemClock.uptimeMillis() - start);
            Log.e(TAG, "FastJson time = " + fastJsonTime);

            start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                String json = mGson.toJson(a);
                Log.e(TAG, "gson = " + json);
            }
            gsonTime = (SystemClock.uptimeMillis() - start);
            Log.e(TAG, "Gson time = " + gsonTime);

            start = SystemClock.uptimeMillis();
            for (int i = 0; i < num; i++) {
                a.toJson();
            }
            originTime = (SystemClock.uptimeMillis() - start);
            Log.e(TAG, "Origin time = " + originTime);
            Log.e(TAG, "" + fastJsonTime + "\t" + gsonTime + "\t" + originTime);
        } catch (Throwable e) {
            Log.e(TAG, "" + Log.getStackTraceString(e));
        }
    }
}
