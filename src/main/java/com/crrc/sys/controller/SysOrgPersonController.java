package com.crrc.sys.controller;

import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.enums.GenderEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.service.SysOrgPersonService;
import com.crrc.sys.service.SysUserService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织架构-人员-控制器
 *
 * @author 谭永强
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/org/person/")
public class SysOrgPersonController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysOrgPersonService orgPersonService;
    @Resource
    private SysUserService userService;

    /**
     * 人员分页查询(Copy)
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage/cp")
    public PaginationGrid<SysOrgPerson> findByPageCopy(SysOrgPersonFindByPageParam param) {
        return findByPage(param);
    }

    /**
     * 人员分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysOrgPerson> findByPage(SysOrgPersonFindByPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        if (ObjectUtils.isEmpty(param.getLanguage())) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(param.getLanguage())) {
            throw new ParamValidateException("language参数错误");
        }
        if (!ObjectUtils.isEmpty(param.getPersonType())) {
            param.setPersonTypeList(Arrays.stream(param.getPersonType().split(",")).map(Long::parseLong).collect(Collectors.toList()));
        }
        return orgPersonService.findByPage(param);
    }

    /**
     * 人员新增
     *
     * @param person 人员
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysOrgPerson person) {
        verification(OperationTypeEnum.ADD.getValue(), person);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        person.setPersonId(idKeyGenerator.getId());
        //创建人
        person.setCreateBy(currentUser.getUserId());
        //创建时间
        person.setCreateDate(Calendar.getInstance().getTime());
        int result = orgPersonService.add(person);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 人员修改
     *
     * @param person 人员
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysOrgPerson person) {
        verification(OperationTypeEnum.UPDATE.getValue(), person);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //创建人
        person.setLastUpdateBy(currentUser.getUserId());
        //创建时间
        person.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改人登录日志
        person.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = orgPersonService.update(person);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 人员删除
     *
     * @param personIds 人员ID，多个以逗号","拼接
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String personIds) {
        if (ObjectUtils.isEmpty(personIds)) {
            throw new ParamValidateException("personIds不能为空");
        }
        List<Long> idList = Arrays.stream(personIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = orgPersonService.delete(idList);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 人员列表查询
     *
     * @param param 查询条件
     * @return 结果
     */
    @PostMapping("findList")
    public List<SysOrgPerson> findList(@RequestBody SysOrgPersonFindByPageParam param) {
        return orgPersonService.findList(param);
    }

    /**
     * 人员新增(列表)
     *
     * @param persons 人员信息
     * @return 结果
     */
    @PostMapping("addList")
    public Boolean add(@RequestBody List<SysOrgPerson> persons) {
        return orgPersonService.addList(persons);
    }

    /**
     * 根据工号查询人员信息
     *
     * @param jobNo 工号
     * @return 人员实体
     */
    @PostMapping("findByJobNo")
    public SysOrgPerson findByJobNo(@RequestParam("jobNo") String jobNo) {
        return orgPersonService.findByJobNo(jobNo);
    }

    /**
     * 根据工号列表查询人员信息
     *
     * @param jobNos 工号列表
     * @return 结果
     */
    @PostMapping("findByJobNos")
    public List<SysOrgPerson> findByJobNos(@RequestBody List<String> jobNos) {
        return orgPersonService.findByJobNos(jobNos);
    }

    /**
     * 离职日期监控(当查询到离职时间<=当前时间，则修改状态为离职)
     * 该接口提供给定时任务访问
     */
    @PostMapping("expirationTateMonitor")
    public void expirationTateMonitor() {
        //查询[离职时间<=当前时间]的人员
        List<SysOrgPerson> rows = orgPersonService.findByQuitDate(Calendar.getInstance().getTime());
        if (!CollectionUtils.isEmpty(rows)) {
            List<Long> personIdList = rows.stream().map(SysOrgPerson::getPersonId).collect(Collectors.toList());
            //修改人员状态
            orgPersonService.updateStatus(personIdList, 2);
        }
    }

    /**
     * 根据当前用户查询人员信息
     *
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCurrentUser")
    public SysOrgPerson findByCurrentUser(String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        SysUser currentUser = currentUserResultData.getResult();
        if (!currentUserResultData.getSuccess() || ObjectUtils.isEmpty(currentUser)) {
            throw new BizException("当前用户获取失败!");
        }
        return findByUserId(currentUser.getUserId(), language);
    }

    /**
     * 根据用户ID查询人员信息
     *
     * @param userId   人员ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByUserId")
    public SysOrgPerson findByUserId(Long userId, String language) {
        if (ObjectUtils.isEmpty(userId)) {
            throw new ParamValidateException("userId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        SysUser sysUser = userService.findById(userId, language);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new BizException("用户不存在!");
        }
        if (ObjectUtils.isEmpty(sysUser.getUserRelationId())) {
            throw new BizException("用户未关联员工!");
        }
        return orgPersonService.findById(sysUser.getUserRelationId(), language);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param person    人员
     */
    private void verification(String operation, SysOrgPerson person) {
        if (ObjectUtils.isEmpty(person)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(person.getJobNo())) {
            throw new ParamValidateException("工号不能为空!");
        }
        if (person.getJobNo().length() > 20) {
            throw new ParamValidateException("工号超长(最大20)!");
        }
        // 根据工号查询人员信息
        SysOrgPerson byJobNo = orgPersonService.findByJobNo(person.getJobNo());
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            if (!ObjectUtils.isEmpty(byJobNo)) {
                throw new ParamValidateException("工号已存在");
            }
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(person.getPersonId())) {
                throw new ParamValidateException("personId不能为空!");
            }
            if (ObjectUtils.isEmpty(person.getLanguage())) {
                throw new ParamValidateException("language不能为空");
            }
            //处理前端请求，无缘无故多个逗号的问题
            person.setLanguage(person.getLanguage().substring(0, 5));
            if (!LanguageEnum.contains(person.getLanguage())) {
                throw new ParamValidateException("language参数错误");
            }
        }
        if (ObjectUtils.isEmpty(person.getFullName())) {
            throw new ParamValidateException("姓名不能为空!");
        }
        if (person.getFullName().length() > 30) {
            throw new ParamValidateException("姓名超长(最大30)!");
        }
        if (ObjectUtils.isEmpty(person.getGender())) {
            throw new ParamValidateException("性别不能为空!");
        }
        if (!GenderEnum.contains(person.getGender())) {
            throw new ParamValidateException("性别参数错误!");
        }
        if (!ObjectUtils.isEmpty(person.getPhone()) && person.getPhone().length() > 11) {
            throw new ParamValidateException("手机超长(最大11)!");
        }
        //邮箱校验
        String emailReg = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        if (!ObjectUtils.isEmpty(person.getEmail()) && !person.getEmail().matches(emailReg)) {
            throw new ParamValidateException("邮箱格式错误!");
        }
        if (person.getEmail().length() > 50) {
            throw new ParamValidateException("邮箱超长(最大50)!");
        }
        if (ObjectUtils.isEmpty(person.getStatus())) {
            throw new ParamValidateException("人员状态不能为空");
        }
    }
}