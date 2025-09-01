package com.crrc.sys.controller;

import com.crrc.sys.entity.role.SysRoleColumnValueSet;
import com.crrc.sys.service.SysRoleColumnValueSetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-09
 */
@RestController
@RequestMapping("/role/column/value/set/")
public class SysRoleColumnValueSetController {

	@Resource
	private SysRoleColumnValueSetService roleColumnValueSetService;

	/**
	 * 根据角色ID和块编码查询值集信息
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 结果
	 */
	@PostMapping("findByRoleIdAndBlockCode")
	public List<SysRoleColumnValueSet> findByRoleIdAndBlockCode(long roleId, String blockCode) {
		return roleColumnValueSetService.findByRoleIdAndBlockCode(roleId, blockCode);
	}
}
