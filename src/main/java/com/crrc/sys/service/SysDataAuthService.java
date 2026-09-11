package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.model.profile.ProfileBindObjectDTO;
import com.crrc.sys.model.profile.ProfileBindObjectQueryDTO;
import com.crrc.sys.model.profile.ProfileBindObjectVO;

import java.util.List;
import java.util.Map;

/**
 * @author 谭永强
 * @date 2023-03-23
 */
public interface SysDataAuthService {

	/**
	 * 根据对象ID删除数据权限
	 *
	 * @param objectId 对象ID
	 * @return 结果
	 */
	List<SysDataAuth> findByObjectId(long objectId);

    Map<String, List<String>> findDataAuthsByObjectId(Long userId, String value);

	Map<String, List<String>> getDataAuth();

	/**
	 * 分页查询配置文件已绑定的角色/用户(配置文件维度)
	 * 支持按对象名称/编码模糊查询(objectSearchParam)与权限值过滤(authValue)，命中对象内嵌其全部权限值集合。
	 *
	 * @param query 查询入参(profileId、authType、language、分页、objectSearchParam、authValue)
	 * @return 分页绑定对象集合(含名称、编码、权限值集合)
	 */
	PaginationGrid<ProfileBindObjectVO> findBindObjects(ProfileBindObjectQueryDTO query);

	/**
	 * 保存配置文件绑定的角色/用户(配置文件维度，增量语义：仅处理前端传递的对象)
	 * 前端按对象维度传递需要处理的对象集合(每项=对象ID+该对象拥有的权限值)，后端仅对 bindList 中出现的
	 * 对象做处理，不删除前端未传递的对象绑定。对每个对象的权限值按 authValue 唯一键做多增少删；
	 * 前端传空权限值的已存在对象会被级联删除主表记录及权限值，避免出现有数据权限但权限值为空的脏数据。
	 *
	 * @param dto       绑定入参
	 * @param createBy  创建人ID
	 */
	void saveBindObjects(ProfileBindObjectDTO dto, long createBy);
}
