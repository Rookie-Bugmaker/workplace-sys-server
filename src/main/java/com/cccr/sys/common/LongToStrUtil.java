package com.cccr.sys.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @author 谭永强
 * @date 2022-09-15
 */
public enum LongToStrUtil {

    /**
     * 实例
     */
    INSTANCE;

    private static final long JS_MAX_LONG_VALUE = 9007199254740992L;

    /**
     * 转换json对象中的Long数据
     *
     * @param jsonObject json对象
     * @return 转换后的json对象
     */
    private JSON coverObj(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object obj = entry.getValue();
            if (obj == null) {
                continue;
            }
            obj = this.cover(obj);
            entry.setValue(obj);
        }
        return jsonObject;
    }


    /**
     * 转换json数组中的Long数据
     *
     * @param array json数组对象
     * @return 转换后的json数组对象
     */
    private JSON coverArr(JSONArray array) {
        for (int i = 0, size = array.size(); i < size; i++) {
            Object obj = array.get(i);
            obj = this.cover(obj);
            array.set(i, obj);
        }
        return array;
    }

    /**
     * 转换Long为字符串
     *
     * @param obj 带转换对象
     * @return 转换后对象
     */
    public Object cover(Object obj) {
        if (obj instanceof Long) {
            Long val = (Long) obj;
            if (val > JS_MAX_LONG_VALUE) {
                obj = val.toString();
            }
        } else if (obj instanceof Collection) {
            obj = this.coverArr(JSON.parseArray(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue)));
        } else if (JsonUtil.INSTANCE.isJsonObj(obj)) {
            obj = this.coverObj(JSON.parseObject(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue)));
        } else if (obj instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) obj;
            long longValue = bigDecimalValue.longValue();
            if (longValue > JS_MAX_LONG_VALUE) {
                obj = String.valueOf(longValue);
            }
        }
        return obj;
    }
}
