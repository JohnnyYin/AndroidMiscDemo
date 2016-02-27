package com.johnnyyin.protobufdemo;

import com.google.gson.Gson;
import com.squareup.wire.Message;
import com.squareup.wire.Wire;

import java.io.IOException;

/**
 * 所有实体类的基类，提供一些常用的序列化和反序列化方法
 */
public abstract class TTMessage extends Message {
    private static Gson mGson = new Gson();
    private static Wire mWire = new Wire();

    /**
     * 从Json反序列化
     *
     * @param json     json字符串
     * @param classOfT 实体类
     * @param <T>      实体类的实际类型
     * @return 实体类的实例
     */
    public static <T> T parseFrom(String json, Class<T> classOfT) {
        return mGson.fromJson(json, classOfT);
    }

    /**
     * 从Protobuf反序列化
     *
     * @param bytes    Protobuf的byte数组
     * @param classOfT 实体类
     * @param <T>      实体类的实际类型
     * @return 实体类的实例
     */
    public static <T extends Message> T parseFrom(byte[] bytes, Class<T> classOfT) {
        try {
            return mWire.parseFrom(bytes, classOfT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 序列化为Json格式
     *
     * @return json字符串
     */
    public String toJson() {
        return mGson.toJson(this);
    }

}
