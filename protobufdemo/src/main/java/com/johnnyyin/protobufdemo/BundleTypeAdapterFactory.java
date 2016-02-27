package com.johnnyyin.protobufdemo;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class BundleTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public BundleTypeAdapterFactory() {
        constructorConstructor = new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Bundle.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        ObjectConstructor<T> constructor = constructorConstructor.get(type);
        return (TypeAdapter<T>) new Adapter(gson, (ObjectConstructor<? extends Bundle>) constructor);
    }

    private final class Adapter extends TypeAdapter<Bundle> {
        private final TypeAdapter<String> keyTypeAdapter;
        private final TypeAdapter<Object> valueTypeAdapter;
        private final ObjectConstructor<? extends Bundle> constructor;

        public Adapter(Gson context, ObjectConstructor<? extends Bundle> constructor) {
            this.keyTypeAdapter = context.getAdapter(TypeToken.get(String.class));
            this.valueTypeAdapter = context.getAdapter(TypeToken.get(Object.class));
            this.constructor = constructor;
        }

        @Override
        public void write(JsonWriter out, Bundle value) throws IOException {

        }

        @Override
        public Bundle read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            Bundle bundle = constructor.construct();

            if (peek == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                while (in.hasNext()) {
                    in.beginArray(); // entry array
                    String key = keyTypeAdapter.read(in);
                    Object value = valueTypeAdapter.read(in);
                    putToBundle(bundle, key, value);
                    in.endArray();
                }
                in.endArray();
            } else {
                in.beginObject();
                while (in.hasNext()) {
                    JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                    String key = keyTypeAdapter.read(in);
                    Object value = valueTypeAdapter.read(in);
                    putToBundle(bundle, key, value);
                }
                in.endObject();
            }
            return bundle;
        }
    }

    private void putToBundle(Bundle bundle, String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof ArrayList) {
            bundle.putSerializable(key, (ArrayList) value);
        } else if (value instanceof LinkedTreeMap) {
            bundle.putSerializable(key, (LinkedTreeMap) value);
        } else {
            // do nothing.
        }
    }
}
