package com.populstay.populife.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GsonUtil {

    public static <T> T fromJson(String jsonStr, Class<T> cls) {
        return new Gson().fromJson(jsonStr, cls);
    }

    public static <T> List<T> fromJson(String jsonStr, TypeToken<List<T>> typeToken) {
        return new Gson().fromJson(jsonStr, typeToken.getType());
    }


}
