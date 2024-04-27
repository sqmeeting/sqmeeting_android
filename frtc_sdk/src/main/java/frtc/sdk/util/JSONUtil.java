package frtc.sdk.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JSONUtil {

    private static final Gson gsonObj = new Gson();

    public static String toJSONString(Object src) {
        //
        return gsonObj.toJson(src);
    }

    public static <T> T fromJson(String json, Type type) {
        //
        return gsonObj.fromJson(json, type);
    }

    public static <T> T transform(String json, Class<T> type) {
        //
        return gsonObj.fromJson(json, type);
    }

}
