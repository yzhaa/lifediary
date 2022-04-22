package com.yzh.myjson;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Gson {

    private static Gson gson;


    private Gson() {

    }

    public static Gson getGson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    public String toJson(Object o) {
        try {
            return judgeAndExecuteSerial(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public <T> T fromJson(String str, Type type) {
        try {
            return (T) judgeAndExecuteDeSerial(str, type);
        } catch (IllegalAccessException | InstantiationException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private List<?> fromJsonList(JSONArray jsonArray, Type type) throws JSONException, IllegalAccessException, InstantiationException {
        List<Object> list = new ArrayList<>(jsonArray.length());

        if (type instanceof ParameterizedType) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(fromJsonList(jsonArray.getJSONArray(i), ((ParameterizedType) type).getActualTypeArguments()[0]));

            }
        } else {

            if (primary(type) || type == String.class || type == boolean.class) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    //注意这里使用get（i）
                    list.add(jsonArray.get(i));
                }
            } else {
                Map<Field, String> map = getDeSerialMapping(typeToClass(type));
                for (int i = 0; i < jsonArray.length(); i++) {

                    list.add(fromJsonObject(map, jsonArray.getJSONObject(i), typeToClass(type)));
                }
            }

        }
        return list;
    }

    private Object fromJsonObject(Map<Field, String> map, JSONObject jsonObject, Class<?> z) {

        Object o = UnsafeHelper.newInstance(z);
        for (Field f : map.keySet()) {
            if (f.getGenericType() instanceof ParameterizedType) {
                Object v;
                try {
                    v = fromJsonList(jsonObject.getJSONArray(map.get(f)), ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]);
                    f.set(o, v);
                } catch (JSONException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    try {
                        f.set(o, null);
                    } catch (IllegalAccessException g) {
                        g.printStackTrace();
                    }
                }
            } else {
                try {
                    getObjectWithFiled(f, o, jsonObject.get(map.get(f)));
                } catch (JSONException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    try {
                        getObjectWithFiled(f, o, null);
                    } catch (JSONException | IllegalAccessException | InstantiationException g) {
                        g.printStackTrace();
                    }
                }
            }
        }
        return o;
    }


    private void getObjectWithFiled(Field field, Object o, Object value) throws IllegalAccessException, JSONException, InstantiationException {

        //还不够完善
        if (value == null || value.equals("") || value.toString().equals("null")) {
            Log.d("TestNull", "getObjectWithFiled: ");

            if (primary(field.getType())) {
                field.set(o, 0);
            } else if (field.getType() == boolean.class) {
                field.set(o, false);
            } else {
                field.set(o, null);
            }

        } else if (primary(field.getType())) {
            field.set(o, Integer.valueOf(value.toString()));
        } else if (field.getType() == String.class || field.getType() == boolean.class) {
            field.set(o, value);
        } else {
            field.set(o, fromJsonObject(getDeSerialMapping(field.getType()), new JSONObject(value.toString()), typeToClass(field.getType())));
        }
    }

    private boolean primary(Type type) {

        return (type== int.class || type == float.class
                || type == double.class || type == short.class);
    }

    private void handlerObject(Map<Field, String> fieldStringMap, Object o, StringBuilder sb) throws
            IllegalAccessException {
        sb.append("{");
        for (Field f : fieldStringMap.keySet()) {
            String key = Objects.requireNonNull(fieldStringMap.get(f));

            sb.append("\"").append(key).append("\"").append(":");
            if (List.class.isAssignableFrom(f.getType())) {
                handlerList((List<List>) f.get(o), sb);
            } else {
                if (String.class.isAssignableFrom(f.getType())) {
                    sb.append("\"").append(f.get(o)).append("\"");
                } else {
                    addString(f, sb, o);
                }
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append("}");

    }

    private void addString(Field field, StringBuilder sb, Object o) throws
            IllegalAccessException {

        if (field.getType() == String.class || field.getType() == int.class || field.getType() == float.class
                || field.getType() == double.class || field.getType() == short.class || field.getType() == boolean.class) {
            sb.append(field.get(o));
        } else {
            handlerObject(getSerialMapping(field.getType()), field.get(o), sb);
        }

    }


    private void handlerList(List<?> list, StringBuilder sb) throws IllegalAccessException {
        sb.append("[");
        if (list != null && list.size() > 0) {
            Class<?> c = list.get(0).getClass();
            if (List.class.isAssignableFrom(c)) {
                for (List l : (List<List>) list) {
                    handlerList(l, sb);
                    sb.append(",");
                }
            } else {
                Map<Field, String> map = getSerialMapping(c);
                for (Object o : list) {

                    handlerObject(map, o, sb);
                    sb.append(",");
                }

            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }


    private Object judgeAndExecuteDeSerial(String str, Type type) throws
            IllegalAccessException, JSONException, InstantiationException {
        if (type instanceof ParameterizedType) {
            if ((List.class.isAssignableFrom(((ParameterizedType) type).getRawType().getClass()))) {
                return fromJsonList(new JSONArray(str), ((ParameterizedType) type).getActualTypeArguments()[0]);
            } else {
                return judgeAndExecuteDeSerial(str, ((ParameterizedType) type).getRawType());
            }
        } else if (type instanceof Class) {
            return fromJsonObject(getDeSerialMapping((Class<?>) type), new JSONObject(str), typeToClass(type));
        } else return null;
    }


    private String judgeAndExecuteSerial(Object o) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        Class<?> z = o.getClass();
        if (List.class.isAssignableFrom(z)) {
            handlerList((List<List>) o, stringBuilder);
        } else {
            handlerObject(getSerialMapping(z), o, stringBuilder);
        }
        return stringBuilder.toString();
    }


    private Map<Field, String> getSerialMapping(Class<?> tClass) {

        Stream<Field> s1 = Arrays.stream(tClass.getFields());
        Stream<Field> s2 = Arrays.stream(tClass.getDeclaredFields());
        List<Field> list = Stream.concat(s1, s2).distinct().collect(Collectors.toList());

        Map<Field, String> map = new HashMap<>(list.size());
        for (Field f : list) {
            Expose a = f.getAnnotation(Expose.class);
            if (a != null) {
                if (!a.serialize()) {
                    continue;
                }
            }
            SerializedName s = f.getAnnotation(SerializedName.class);
            if (s != null && !s.value().equals("")) {
                map.put(f, s.value());
            } else {
                map.put(f, f.getName());
            }
            f.setAccessible(true);
        }
        return map;
    }


    private Map<Field, String> getDeSerialMapping(Class<?> tClass) {


        Stream<Field> s1 = Arrays.stream(tClass.getFields());
        Stream<Field> s2 = Arrays.stream(tClass.getDeclaredFields());
        List<Field> list = Stream.concat(s1, s2).distinct().collect(Collectors.toList());
        Map<Field, String> map = new HashMap<>(list.size());
        for (Field f : list) {
            Expose a = f.getAnnotation(Expose.class);
            if (a != null) {
                if (!a.deserialize()) {
                    continue;
                }
            }
            SerializedName s = f.getAnnotation(SerializedName.class);
            if (s != null && !s.value().equals("")) {
                map.put(f, s.value());
            } else {
                map.put(f, f.getName());
            }
            f.setAccessible(true);
        }
        return map;
    }

    private Class<?> typeToClass(Type type) {
        if (type instanceof WildcardType) {

            Type t = ((WildcardType) type).getUpperBounds()[0];
            if (t instanceof Class) {
                return (Class<?>) t;
            }
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            return null;
        }

    }
}
