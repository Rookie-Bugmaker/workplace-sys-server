package com.yunling.sys.service.homepage.imp;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunling.sys.common.IdKeyGenerator;
import com.yunling.sys.common.PaginationGrid;
import com.yunling.sys.entity.homepage.HomePageParam;
import com.yunling.sys.entity.homepage.SysHomePageComponent;
import com.yunling.sys.entity.homepage.SysHomePageComponentUser;
import com.yunling.sys.entity.homepage.SysHomePageComponentUserParam;
import com.yunling.sys.entity.user.SysUser;
import com.yunling.sys.enums.LanguageEnum;
import com.yunling.sys.mapper.SysHomePageComponentMapper;
import com.yunling.sys.service.SysUserService;
import com.yunling.sys.service.homepage.SysHomePageComponentService;
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

	@Resource
	private SysHomePageComponentMapper sysHomePageComponentMapper;

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
