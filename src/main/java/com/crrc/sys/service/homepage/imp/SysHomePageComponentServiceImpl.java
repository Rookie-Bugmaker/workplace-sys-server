package com.crrc.sys.service.homepage.imp;

import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.entity.homepage.SysHomePageComponent;
import com.crrc.sys.entity.homepage.SysHomePageComponentUserParam;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.mapper.mapperImpl.SysHomePageComponentMapperImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.crrc.sys.entity.homepage.HomePageParam;
import com.crrc.sys.entity.homepage.SysHomePageComponentUser;
import com.crrc.sys.service.SysUserService;
import com.crrc.sys.service.homepage.SysHomePageComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 主页组件定义表 服务实现类
 * </p>
 *
 * @author fucheng
 * @since 2023-05-19
 */
@Service
public class SysHomePageComponentServiceImpl  implements SysHomePageComponentService {

	@Resource
	private IdKeyGenerator idKeyGenerator;

	@Autowired
	private SysHomePageComponentMapperImpl sysHomePageComponentMapper;

	@Resource
	private SysUserService sysUserService;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer add(SysHomePageComponent param) {
		param.setComponentId(idKeyGenerator.getId());
		return sysHomePageComponentMapper.add(param);
	}

	@Override
	public PaginationGrid<SysHomePageComponent> pageList(HomePageParam param) {
		PaginationGrid<SysHomePageComponent> page = new PaginationGrid<>(param.getPage(), param.getPageSize());
		PageHelper.startPage(page.getPage(), page.getPageSize());
		List<SysHomePageComponent> rows = sysHomePageComponentMapper.findList(param);
		PageInfo<SysHomePageComponent> pageInfo = new PageInfo<>(rows);
		return PaginationGrid.success(pageInfo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer deleteById(Long componentId) {
		return sysHomePageComponentMapper.deleteById(componentId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateById(SysHomePageComponent param) {
		return sysHomePageComponentMapper.updateById(param);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer addUser(SysHomePageComponentUserParam param) {
		//先删除再新增
		sysHomePageComponentMapper.deleteUserByComponentId(param.getComponentId());
		List<SysHomePageComponentUser> list = new ArrayList<>();
		String[] userIds = param.getUserIds().split(",");
		Date date = new Date();
		for (String userId : userIds){
			SysHomePageComponentUser componentUser = new SysHomePageComponentUser();
			componentUser.setComponentUserId(idKeyGenerator.getId());
			componentUser.setUserId(Long.valueOf(userId));
			componentUser.setComponentId(param.getComponentId());
			componentUser.setCreateDate(date);
			list.add(componentUser);
		}
		return sysHomePageComponentMapper.addUser(list);
	}

	@Override
	public List<Long> findUserByComponentId(Long componentId) {
		return sysHomePageComponentMapper.findUserByComponentId(componentId);
	}

	@Override
	public List<SysHomePageComponent> findComponentByUserId(Long userId) {
		List<SysHomePageComponent> resList = new ArrayList<>();
		List<SysHomePageComponent> componentByUserId = sysHomePageComponentMapper.findComponentByUserId(userId);
		SysUser user = sysUserService.findById(userId, LanguageEnum.ZH_CN.getValue());
		List<SysHomePageComponent> componentByUserType = sysHomePageComponentMapper.findComponentByUserType(user.getUserType());
		resList.addAll(componentByUserId);
		resList.addAll(componentByUserType);
		return resList;
	}
}
