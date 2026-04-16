package com.crrc.sys.entity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量新增用户结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchAddResult {

    /**
     * 总数量
     */
    private Integer totalCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败详情列表
     */
    private List<AddFailure> failures;

    /**
     * 失败信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddFailure {
        /**
         * 用户索引（从 0 开始）
         */
        private Integer index;

        /**
         * 用户名
         */
        private String userName;

        /**
         * 失败原因
         */
        private String errorMsg;
    }
}