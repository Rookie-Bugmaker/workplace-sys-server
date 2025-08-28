package com.yunling.sys.mapper;

import com.yunling.sys.entity.org.person.SysOrgPerson;
import com.yunling.sys.entity.org.person.SysOrgPersonFindByPageParam;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
public interface SysOrgPersonMapper {

	/**
	 * 人员查询
	 *
	 * @param param 参数
	 * @return 结果
	 */
	List<SysOrgPerson> findByFuzzy(SysOrgPersonFindByPageParam param);

	/**
	 * 人员新增
	 * @param person 人员信息
	 * @return 结果
	 */
	int insert(SysOrgPerson person);

	/**
	 * 人员修改
	 * @param person 人员信息
	 * @return 结果
	 */
	int update(SysOrgPerson person);

	/**
	 * 根据人员ID查询人员信息
	 *
	 * @param personId 人员ID
	 * @param language 语言
	 * @return 结果
	 */
	SysOrgPerson findById(@Param("personId") long personId, @Param("language") String language);

	/**
	 * 人员删除
	 * @param personIdList 人员ID
	 * @return 结果
	 */
	int delete(@Param("personIdList") List<Long> personIdList);

	/**
	 * 根据工号查询人员信息
	 * @param jobNo 工号
	 * @return 人员实体
	 */
    SysOrgPerson findByJobNo(@Param("jobNo") String jobNo);

/**
	 * 根据工号列表查询人员信息
	 * @param jobNos 工号列表
	 * @return 人员实体列表
	 */
    List<SysOrgPerson> findByJobNos(@Param("jobNos") List<String> jobNos);


	/**
	 * 按人员id列表查询人员信息
	 * @param personIdList 人员id列表
	 */
	List<SysOrgPerson> findByPersonIds(@Param("personIdList") List<Long> personIdList,@Param("language")String language);

	/**
	 * 根据人员id集合查询人员集合
	 * @param personIds 人员id集合
	 * @param language  语言
	 * @return 结果
	 */
    List<SysOrgPerson> findByIds(@Param("personIds") List<Long> personIds, @Param("language") String language);


	/**
	 * 人员新增(列表)
	 *
	 * @param persons 人员信息
	 * @return 结果
	 */
	boolean insertBatch(List<SysOrgPerson> persons);

	/**
	 * 查询[离职时间<=当前时间]的人员
	 *
	 * @param nowDate 当前时间
	 * @return 人员集合
	 */
    List<SysOrgPerson> findByQuitDate(@Param("nowDate") Date nowDate);

	/**
	 * 修改人员状态
	 *
	 * @param personIdList 人员ID
	 * @param status        状态
	 */
	void updateStatus(@Param("personIdList") List<Long> personIdList, @Param("status") int status);

	/**
	 * 判断手机号是否存在
	 *
	 * @param phoneList 手机号集合
	 * @return 已存在的手机号
	 */
    List<SysOrgPerson> findByPhoneList(@Param("phoneList") List<String> phoneList);
}
