package com.crrc.sys.model.role;

import lombok.Data;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-11-14
 */
@Data
public class BindUserDTO {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 用户ID集合
     */
    private List<Long> userIdList;
}
