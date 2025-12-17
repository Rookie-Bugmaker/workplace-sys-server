package com.crrc.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.UserExcelEntity;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户验证方法服务
 *
 * @author 谭永强
 * @date 2022-09-02
 */
public interface SysUserValidService {

    List<SysUser> validateBuild(List<UserExcelEntity> list);

    void verification(String operation, SysUser user);

    int checkStrong(String pwd, String userName, String fullName);
}
