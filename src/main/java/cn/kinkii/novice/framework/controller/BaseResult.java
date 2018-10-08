package cn.kinkii.novice.framework.controller;

import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data(staticConstructor = "build")
public class BaseResult {

    @NonNull
    private Integer code;

    @NonNull
    private String message;
    private Map<String, Object> values;

    public static BaseResult success(String message) {
        return BaseResult.build(0, message);
    }

    public static BaseResult failure(Integer code, String message) {
        return BaseResult.build(code, message);
    }

    public BaseResult addValue(String key, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(key, value);

        return this;
    }

}
