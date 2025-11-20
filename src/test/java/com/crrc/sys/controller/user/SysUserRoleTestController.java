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
@RequestMapping("/test/userRole")
@Tag(name = "用户角色关联测试接口", description = "用户角色关联相关操作的测试接口")
public class SysUserRoleTestController {

    @Autowired
    private SysUserRoleMapperImpl sysUserRoleMapper;

    @Operation(summary = "根据用户ID和语言查询角色列表")
    @GetMapping("/findByUserId")
    public List<SysRole> findByUserId(@RequestParam long userId, @RequestParam String language) {
        return sysUserRoleMapper.findByUserId(userId, language);
    }

    @Operation(summary = "批量插入用户角色关联")
    @PostMapping("/insert")
    public int insert(@RequestBody List<SysUserRole> userRoleList) {
        return sysUserRoleMapper.insert(userRoleList);
    }

    @Operation(summary = "根据用户ID删除关联")
    @DeleteMapping("/deleteByUserId")
    public int deleteByUserId(@RequestParam long userId) {
        return sysUserRoleMapper.deleteByUserId(userId);
    }

    @Operation(summary = "根据用户ID查询角色ID列表")
    @GetMapping("/findRoleIdByUserId")
    public List<Long> findRoleIdByUserId(@RequestParam long userId) {
        return sysUserRoleMapper.findRoleIdByUserId(userId);
    }

    @Operation(summary = "根据角色ID查询用户ID列表")
    @GetMapping("/findUserIdByRoleId")
    public List<Long> findUserIdByRoleId(@RequestParam long roleId) {
        return sysUserRoleMapper.findUserIdByRoleId(roleId);
    }

    @Operation(summary = "根据用户ID或用户名查询用户角色关联")
    @PostMapping("/findUserRoleByUserIdOrUserName")
    public List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(@RequestBody IdNameCollect idNameCollect) {
        return sysUserRoleMapper.findUserRoleByUserIdOrUserName(idNameCollect.getUserIds(), idNameCollect.getUserNames());
    }

    @Operation(summary = "根据角色ID列表删除关联")
    @DeleteMapping("/deleteByRoleIds")
    public void deleteByRoleIds(@RequestBody List<Long> roleIdList) {
        sysUserRoleMapper.deleteByRoleIds(roleIdList);
    }

    @Operation(summary = "根据用户ID列表查询用户角色关联")
    @PostMapping("/findByUserIds")
    public List<SysUserRolePO> findByUserIds(@RequestBody List<Long> userIdList) {
        return sysUserRoleMapper.findByUserIds(userIdList);
    }

    @Data
    static class IdNameCollect {
        private List<Long> userIds;
        private List<String> userNames;
    }
}