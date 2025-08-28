package com.yunling.sys.entity.excel;


/**
 * @ClassName: ExcelResultData
 * @Description: excel处理数据
 * @Author LiuQiang
 * @Date 2023/5/28
 */
public class ExcelResultData<T> {

    public String redisKey;

    public T data;

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回的数据
     * @param <T>  泛型
     * @return 结果
     */
    public static <T> ExcelResultData<T> build(String redisKey,T data) {
        ExcelResultData<T> resultData = new ExcelResultData<>();
        resultData.setRedisKey(redisKey);
        resultData.setData(data);
        return resultData;
    }
}
