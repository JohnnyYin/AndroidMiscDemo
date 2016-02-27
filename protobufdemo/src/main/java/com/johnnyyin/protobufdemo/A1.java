package com.johnnyyin.protobufdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 1.必须有无参构造
 * 2.无参构造可以为任意的访问权限，包括private
 */
public class A1 {
    private String str;
    private byte b;
    private char c;
    private short s;
    private int i = 6;
    private long l;
    private float f;
    private double d;
    private boolean bool;
    private B bo;
    //    private byte[] byteArray;
    private List<List<B>> list;
    private Set<Set<B>> set;
    private Queue<Queue<B>> queue;
    private B[] array;
    private Map<String, Map<String, B>> map;
//    @SerializedInfo("sArray")
//    private SparseArray<SparseArray<B>> sparseArray;
//    private JSONObject jsonObject;
//    @SerializedInfo(sourceCls = JSONArray.class)
//    private String jsonArray;
//    @SerializedInfo(hostCls = JSONObject.class, hostName = "host")
//    private String hostStr;
//    @SerializedInfo(hostCls = JSONObject.class, hostName = "host")
//    private B hostB;

    private A1() {
    }

    public A1(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            str = jo.optString("str");
            b = (byte) jo.optInt("b");
            c = (char) jo.optInt("c");
            s = (short) jo.optInt("s");
            i = jo.optInt("i");
            l = jo.optLong("l");
            f = (float) jo.optDouble("f");
            d = jo.optDouble("d");
            bool = jo.optBoolean("bool");
            B b = new B();
            b.s = jo.optJSONObject("bo").getString("s");
            bo = b;
            JSONArray listArray = jo.optJSONArray("list");
            if (listArray != null) {
                list = new ArrayList<>();
                for (int i = 0; i < listArray.length(); i++) {
                    List<B> l = new ArrayList<B>();
                    JSONArray array = listArray.optJSONArray(i);
                    for (int j = 0; j < array.length(); j++) {
                        B b1 = new B();
                        b1.s = array.optJSONObject(j).getString("s");
                        l.add(b1);
                    }
                    list.add(l);
                }
            }

            JSONArray setArray = jo.optJSONArray("set");
            if (setArray != null) {
                set = new HashSet<>();
                for (int i = 0; i < setArray.length(); i++) {
                    Set<B> s = new HashSet<>();
                    JSONArray array = setArray.optJSONArray(i);
                    for (int j = 0; j < array.length(); j++) {
                        B b1 = new B();
                        b1.s = array.optJSONObject(j).getString("s");
                        s.add(b1);
                    }
                    set.add(s);
                }
            }

            JSONArray queueArray = jo.optJSONArray("queue");
            if (queueArray != null) {
                queue = new LinkedList<>();
                for (int i = 0; i < queueArray.length(); i++) {
                    Queue<B> s = new LinkedList<>();
                    JSONArray array = queueArray.optJSONArray(i);
                    for (int j = 0; j < array.length(); j++) {
                        B b1 = new B();
                        b1.s = array.optJSONObject(j).getString("s");
                        s.add(b1);
                    }
                    queue.add(s);
                }
            }

            JSONArray array1 = jo.optJSONArray("array");
            if (array1 != null) {
                array = new B[array1.length()];
                for (int i = 0; i < array1.length(); i++) {
                    B b1 = new B();
                    b1.s = array1.optJSONObject(i).getString("s");
                    array[i] = b1;
                }
            }

            map = new HashMap<>();
            JSONObject mapAry = jo.optJSONObject("map");
            if (mapAry != null) {
                Iterator<String> iterator = mapAry.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject jsonObject = mapAry.optJSONObject(key);
                    Map<String, B> map2 = new HashMap<>();
                    Iterator<String> iterator2 = jsonObject.keys();
                    while (iterator2.hasNext()) {
                        String key2 = iterator2.next();
                        JSONObject jsonObject2 = jsonObject.optJSONObject(key2);
                        B b1 = new B();
                        b1.s = jsonObject2.getString("s");
                        map2.put(key, b1);
                    }
                    map.put(key, map2);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "A{" +
                "\nstr='" + str + '\'' +
                "\n, b=" + b +
                "\n, c=" + c +
                "\n, s=" + s +
                "\n, i=" + i +
                "\n, l=" + l +
                "\n, f=" + f +
                "\n, d=" + d +
                "\n, bool=" + bool +
                "\n, bo=" + bo +
//                "\n, byteArray=" + Arrays.toString(byteArray) +
//                "\n, byteArray=" + new String(byteArray) +
                "\n, list=" + list +
                "\n, set=" + set +
                "\n, queue=" + queue +
                "\n, array=" + Arrays.toString(array) +
                "\n, map=" + map +
//                "\n, sparseArray=" + sparseArray +
//                "\n, jsonObject=" + jsonObject +
//                "\n, jsonArray=" + jsonArray +
//                "\n, hostStr=" + hostStr +
//                "\n, hostB=" + hostB +
                "\n}";
    }

    public String toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("str", str);
            jo.put("b", b);
            jo.put("c", c);
            jo.put("s", s);
            jo.put("i", i);
            jo.put("l", l);
            jo.put("f", f);
            jo.put("d", d);
            jo.put("bool", bool);
            jo.put("bo", bo.toString());
            JSONArray array = new JSONArray();
            for (List<B> l : list) {
                JSONArray a = new JSONArray();
                for (B b : l) {
                    a.put(b.toString());
                }
                array.put(a);
            }
            jo.put("list", array);

            array = new JSONArray();
            for (List<B> l : list) {
                JSONArray a = new JSONArray();
                for (B b : l) {
                    a.put(b.toString());
                }
                array.put(a);
            }
            jo.put("set", array);

            array = new JSONArray();
            for (List<B> l : list) {
                JSONArray a = new JSONArray();
                for (B b : l) {
                    a.put(b.toString());
                }
                array.put(a);
            }
            jo.put("queue", array);

            array = new JSONArray();
            for (List<B> l : list) {
                JSONArray a = new JSONArray();
                for (B b : l) {
                    a.put(b.toString());
                }
                array.put(a);
            }
            jo.put("array", array);

            array = new JSONArray();
            for (List<B> l : list) {
                JSONArray a = new JSONArray();
                for (B b : l) {
                    a.put(b.toString());
                }
                array.put(a);
            }
            jo.put("map", array);
        } catch (Throwable e) {

        }
        return jo.toString();
    }
}
