package com.crrc.sys.controller.user;

import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.mapper.mapperImpl.SysUserMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysUserRoleMapperImpl;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.model.user.role.SysUserRolePO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/user")
@Tag(name = "用户管理测试接口", description = "用户相关操作的测试接口")
public class SysUserTestController {

    @Autowired
    private SysUserMapperImpl sysUserMapper;

    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/findByUserName")
    public SysUser findByUserName(@RequestParam String userName) {
        return sysUserMapper.findByUserName(userName);
    }

    @Operation(summary = "模糊查询用户列表")
    @PostMapping("/findByFuzzy")
    public List<SysUser> findByFuzzy(@RequestBody SysUserFindByPageParam param) {
        return sysUserMapper.findByFuzzy(param);
    }

    @Operation(summary = "新增用户")
    @PostMapping("/insert")
    public int insert(@RequestBody SysUser user) {
        return sysUserMapper.insert(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public int update(@RequestBody SysUser user) {
        return sysUserMapper.update(user);
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/delete")
    public int delete(@RequestBody List<Long> userIds) {
        return sysUserMapper.delete(userIds);
    }

    @Operation(summary = "根据用户ID查询用户")
    @GetMapping("/findById")
    public SysUser findById(@RequestParam long userId) {
        return sysUserMapper.findById(userId);
    }

    @Operation(summary = "更新最后登录信息ID")
    @PutMapping("/updateLastLoginInfoId")
    public int updateLastLoginInfoId(@RequestParam long loginId, @RequestParam long userId) {
        return sysUserMapper.updateLastLoginInfoId(loginId, userId);
    }

    @Operation(summary = "更新登录错误信息")
    @PutMapping("/updateLoginError")
    public int updateLoginError(@RequestBody SysUser errorUser) {
        return sysUserMapper.updateLoginError(errorUser);
    }

    @Operation(summary = "根据用户ID列表查询用户")
    @PostMapping("/findByUserIds")
    public List<SysUser> findByUserIds(@RequestBody List<Long> userIds) {
        return sysUserMapper.findByUserIds(userIds);
    }

    @Operation(summary = "根据用户ID列表查询用户(带全名)")
    @PostMapping("/findByUserIdListWithFullName")
    public List<SysUserDTO> findByUserIdListWithFullName(@RequestBody List<Long> userIds) {
        return sysUserMapper.findByUserIdListWithFullName(userIds);
    }

    @Operation(summary = "更新用户密码")
    @PutMapping("/updatePassword")
    public int updatePassword(@RequestBody SysUser user) {
        return sysUserMapper.updatePassword(user);
    }

    @Operation(summary = "根据关联ID列表查询用户")
    @PostMapping("/findByRelationIds")
    public List<SysUser> findByRelationIds(@RequestBody List<Long> relationIdList) {
        return sysUserMapper.findByRelationIds(relationIdList);
    }

    @Operation(summary = "根据用户名列表查询用户")
    @PostMapping("/findByUserNameList")
    public List<SysUser> findByUserNameList(@RequestBody List<String> userNameList) {
        return sysUserMapper.findByUserNameList(userNameList);
    }

    @Operation(summary = "根据角色编码查询用户")
    @PostMapping("/findUsersByRoleCode")
    public List<SysUser> findUsersByRoleCode(@RequestBody List<String> roleCode) {
        return sysUserMapper.findUsersByRoleCode(roleCode);
    }

    @Operation(summary = "根据组织编码查询用户")
    @GetMapping("/findUsersByOrgCode")
    public List<SysUser> findUsersByOrgCode(@RequestParam String orgCode, @RequestParam Long companyId) {
        return sysUserMapper.findUsersByOrgCode(orgCode, companyId);
    }

    @Operation(summary = "根据角色编码和公司ID查询用户")
    @GetMapping("/findUserByRoleCodeAndCompanyId")
    public List<SysUserDTO> findUserByRoleCodeAndCompanyId(@RequestParam String roleCode, @RequestParam Long companyId) {
        return sysUserMapper.findUserByRoleCodeAndCompanyId(roleCode, companyId);
    }

    @Operation(summary = "根据角色编码、公司ID和部门ID查询用户")
    @GetMapping("/findUserByRoleCode")
    public List<SysUserDTO> findUserByRoleCode(@RequestParam String roleCode, @RequestParam Long companyId, @RequestParam Long deptId) {
        return sysUserMapper.findUserByRoleCode(roleCode, companyId, deptId);
    }

    @Operation(summary = "根据全名查询用户")
    @GetMapping("/findUsersByfullName")
    public List<SysUserDTO> findUsersByfullName(@RequestParam String fullName, @RequestParam Long companyId) {
        return sysUserMapper.findUsersByfullName(fullName, companyId);
    }

    @Operation(summary = "批量插入用户")
    @PostMapping("/insertAll")
    public boolean insertAll(@RequestBody List<SysUser> list) {
        return sysUserMapper.insertAll(list);
    }

    @Operation(summary = "根据用户ID列表和模糊条件查询用户")
    @PostMapping("/findByUserIdsAndFuzzy")
    public List<SysUser> findByUserIdsAndFuzzy(@RequestBody List<Long> userIdList, 
                                             @RequestParam(required = false) String userName, 
                                             @RequestParam(required = false) String fullName) {
        return sysUserMapper.findByUserIdsAndFuzzy(userIdList, userName, fullName);
    }

    @Operation(summary = "根据用户名列表和模糊条件查询在线用户")
    @PostMapping("/findByUserNamesAndFuzzy")
    public List<SysOnlineUserVO> findByUserNamesAndFuzzy(@RequestBody RequestParamForUser requestParam) {
        return sysUserMapper.findByUserNamesAndFuzzy(requestParam.getUserNameList(), requestParam.getSearch());
    }

    @Data
    static class RequestParamForUser {
        private List<String> userNameList;
        private SysOnlineUserParamDTO search;
    }
}