package com.yzh.myjson;




import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Objects;



public class TypeToken<T> {
    private final Type type;
    protected TypeToken() {
        Type type = getClass().getGenericSuperclass();

        if (type instanceof Class) {
            throw new RuntimeException("请输入正确的类型");
        }
        this.type = Objects.requireNonNull((ParameterizedType)type).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
