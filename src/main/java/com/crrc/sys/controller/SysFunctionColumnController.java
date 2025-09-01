package com.crrc.sys.controller;

import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.enums.EnableFlagEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.service.SysFunctionColumnService;
import com.crrc.sys.service.SysMenuService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 列权限
 *
 * @author 谭永强
 * @date 2022-10-31
 */
@RestController
@RequestMapping("function/column")
public class SysFunctionColumnController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysMenuService menuService;
    @Resource
    private SysFunctionColumnService functionColumnService;

    /**
     * 根据功能ID查询块信息
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 结果
     */
    @PostMapping("findByFunctionId")
    public List<SysFunctionColumn> findByFunctionId(Long functionId, String language) {
        if (ObjectUtils.isEmpty(functionId)) {
            throw new ParamValidateException("functionId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return functionColumnService.findByFunctionIds(Collections.singletonList(functionId), language);
    }

    /**
     * (块/列)新增
     *
     * @param functionColumn 列权限信息
     * @return ID
     */
    @AutoIdempotent
    @PostMapping("add")
    public long add(SysFunctionColumn functionColumn) {
        verification(OperationTypeEnum.ADD.getValue(), functionColumn);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        functionColumn.setFcId(idKeyGenerator.getId());
        //设置创建人
        functionColumn.setCreateBy(currentUser.getUserId());
        //设置创建时间
        functionColumn.setCreateDate(Calendar.getInstance().getTime());
        int result = functionColumnService.add(functionColumn);
        if (result <= 0) {
            throw new BizException("保存失败");
        }
        return functionColumn.getFcId();
    }

    /**
     * (块/列)修改
     *
     * @param functionColumn 列权限信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysFunctionColumn functionColumn) {
        verification(OperationTypeEnum.UPDATE.getValue(), functionColumn);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //设置创建人
        functionColumn.setLastUpdateBy(currentUser.getUserId());
        //设置创建时间
        functionColumn.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        functionColumn.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = functionColumnService.update(functionColumn);
        if (result <= 0) {
            throw new BizException("保存失败");
        }
    }

    /**
     * (块/列)权限删除
     *
     * @param fcIds (块/列)权限ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String fcIds) {
        if (ObjectUtils.isEmpty(fcIds)) {
            throw new ParamValidateException("fcIds参数不能为空");
        }
        List<Long> ids = Arrays.stream(fcIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = functionColumnService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败");
        }
    }

    /**
     * 根据列ID查询列信息(批量)
     *
     * @param columnIds 列ID,多个以逗号","分隔
     * @return 列信息
     */
    @PostMapping("findByColumnIds")
    public List<SysFunctionColumn> findByColumnIds(String columnIds, String language) {
        if (ObjectUtils.isEmpty(columnIds)) {
            throw new ParamValidateException("columnIds参数不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        List<Long> ids = Arrays.stream(columnIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return functionColumnService.findByColumnIds(ids, language);
    }

    /**
     * 根据菜单ID查询列权限
     *
     * @param menuId   菜单ID
     * @param language 列权限
     * @return 结果
     */
    @PostMapping("findByMenuId")
    public List<SysFunctionColumn> findByMenuId(Long menuId, String language) {
        if (ObjectUtils.isEmpty(menuId)) {
            throw new ParamValidateException("menuId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        //根据菜单ID查询功能ID集合
        List<Long> functionIdList = menuService.findFunctionIdByMenuId(menuId);
        if (CollectionUtils.isEmpty(functionIdList)) {
            return null;
        }
        //根据功能ID查询列权限
        return functionColumnService.findByFunctionIds(functionIdList, language);
    }

    /**
     * 根据块编码查询块信息
     *
     * @param blockCode 块编码
     * @param language  语言
     * @return 结果
     */
    @PostMapping("findByBlockCode")
    public SysFunctionColumn findByBlockCode(String blockCode, String language) {
        return functionColumnService.findByBlockCode(blockCode, language);
    }

    /**
     * 根据列编码查询列信息
     *
     * @param columnCode 列编码
     * @param language   语言
     * @param blockId    块ID
     * @return 结果
     */
    @PostMapping("findByColumnCode")
    public SysFunctionColumn findByColumnCode(String columnCode, String language, long blockId) {
        return functionColumnService.findByColumnCode(columnCode, language, blockId);
    }

    /**
     * 根据功能ID查询列权限(批量)
     *
     * @param functionIdList 功能ID集合
     * @param language       语言
     * @return 结果
     */
    @PostMapping("findByFunctionIds")
    public List<SysFunctionColumn> findByFunctionIds(@RequestBody List<Long> functionIdList, String language) {
        return functionColumnService.findByFunctionIds(functionIdList, language);
    }

    /**
     * 参数校验
     *
     * @param operationTyp   类型：add/update
     * @param functionColumn 列权限
     */
    private void verification(String operationTyp, SysFunctionColumn functionColumn) {
        if (ObjectUtils.isEmpty(functionColumn)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operationTyp) && ObjectUtils.isEmpty(functionColumn.getFcId())) {
            throw new ParamValidateException("fcId不能为空!");
        }
        //块
        if (ObjectUtils.isEmpty(functionColumn.getParentId())) {
            if (OperationTypeEnum.ADD.getValue().equals(operationTyp) && ObjectUtils.isEmpty(functionColumn.getFunctionId())) {
                throw new ParamValidateException("functionId不能为空!");
            }
            if (ObjectUtils.isEmpty(functionColumn.getFcCode())) {
                throw new ParamValidateException("块编码不能为空!");
            }
            //根据块编码查询块信息
            SysFunctionColumn blockResultData = functionColumnService.findByBlockCode(functionColumn.getFcCode(), LanguageEnum.ZH_CN.getValue());
            if (!ObjectUtils.isEmpty(blockResultData)) {
                if (OperationTypeEnum.ADD.getValue().equals(operationTyp)) {
                    throw new ParamValidateException("块编码已存在!");
                }
                if (OperationTypeEnum.UPDATE.getValue().equals(operationTyp)) {
                    if (!blockResultData.getFcId().equals(functionColumn.getFcId())) {
                        throw new ParamValidateException("块编码已存在!");
                    }
                }
            }
        }
        //列
        if (!ObjectUtils.isEmpty(functionColumn.getParentId())) {
            if (ObjectUtils.isEmpty(functionColumn.getFcCode())) {
                throw new ParamValidateException("列编码不能为空!");
            }
            //根据列编码查询列信息
            SysFunctionColumn columnResultData = functionColumnService.findByColumnCode(functionColumn.getFcCode(), LanguageEnum.ZH_CN.getValue(), functionColumn.getParentId());
            if (!ObjectUtils.isEmpty(columnResultData)) {
                if (OperationTypeEnum.ADD.getValue().equals(operationTyp)) {
                    throw new ParamValidateException("列编码已存在!");
                }
                if (OperationTypeEnum.UPDATE.getValue().equals(operationTyp)) {
                    if (!columnResultData.getFcId().equals(functionColumn.getFcId())) {
                        throw new ParamValidateException("列编码已存在!");
                    }
                }
            }
            if (ObjectUtils.isEmpty(functionColumn.getFcType())) {
                throw new ParamValidateException("列类型不能为空!");
            }
            if (ObjectUtils.isEmpty(functionColumn.getFcDisplayFlag())) {
                functionColumn.setFcDisplayFlag(EnableFlagEnum.Y.getValue());
            }
            if (ObjectUtils.isEmpty(functionColumn.getFcEditableFlag())) {
                functionColumn.setFcEditableFlag(EnableFlagEnum.Y.getValue());
            }
            if (OperationTypeEnum.ADD.getValue().equals(operationTyp) && ObjectUtils.isEmpty(functionColumn.getParentId())) {
                throw new ParamValidateException("parentId不能为空!");
            }
        }
    }
}
