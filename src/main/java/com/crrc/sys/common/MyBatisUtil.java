package com.crrc.sys.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class MyBatisUtil {

    /**
     * 将MySQL的LIMIT offset,endset参数转换为分页对象
     * @param offset LIMIT起始位置(从0开始)
     * @param endset LIMIT结束位置
     * @return 分页对象
     * @throws IllegalArgumentException 当offset > endset时抛出异常
     */
    public static <T> Page<T> convertToPage(long offset, long endset) {
        // 参数校验
        if (offset < 0 || endset < 0) {
            throw new IllegalArgumentException("起始位置和结束位置不能为空");
        }
        if (offset > endset) {
            throw new IllegalArgumentException("结束位置必须大于起始位置");
        }

        // 计算当前页码和每页大小
        long size = endset;  // 每页记录数
        long current = 1;  // 当前页码(从1开始)
        if (offset > 0) {
            // 非第一页情况
            size = endset - offset;
            current = (offset / size) + 1;
        }
        // 创建并返回Page对象
        return new Page<>(current, size);
    }
}
