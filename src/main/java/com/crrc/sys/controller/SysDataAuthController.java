package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.DataAuthTypeEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.model.profile.ProfileBindObjectDTO;
import com.crrc.sys.model.profile.ProfileBindObjectQueryDTO;
import com.crrc.sys.model.profile.ProfileBindObjectVO;
import com.crrc.sys.service.SysDataAuthService;
import com.crrc.sys.service.SysProfileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限-控制器
 *
 * @author 谭永强
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/data/auth/")
public class SysDataAuthController {

    @Resource
    private SysDataAuthService dataAuthService;

    @Resource
    private AuthClient authClient;




    /**
     * 获取产线列表
     */
    @GetMapping("getProductionLineList")
    public List<Map<String, Object>> getProductionLineList() {
        String str = "[{\"id\":101,\"code\":\"DT_01\",\"name\":\"焊接生产1线\"},{\"id\":102,\"code\":\"DT_02\",\"name\":\"焊接生产2线\"},{\"id\":103,\"code\":\"DT_03\",\"name\":\"焊接生产3线\"},{\"id\":104,\"code\":\"DT_04\",\"name\":\"焊接生产4线\"}]";
        return JSON.parseObject(str, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 获取当前用户数据权限集合
     *
     * @return 结果: {"数据权限编码": ["数据权限值","数据权限值"]}。
     */
    @GetMapping("getDataAuth")
    public Map<String, List<String>> getDataAuth() {
        return dataAuthService.getDataAuth();
    }

    /**
     * 分页查询配置文件已绑定的角色/用户(配置文件维度)
     * 支持按对象名称/编码模糊查询(objectSearchParam)与权限值过滤(authValue)，命中对象内嵌其在该配置文件下的全部权限值集合。
     * <p>objectSearchParam：authType=user 匹配用户名或用户描述(姓名)；authType=role 匹配角色编码或角色名称。</p>
     * <p>authValue：仅返回拥有该权限值的对象(对象级过滤)，命中对象在 authValues 中仍展示其全部权限值。</p>
     *
     * @param query 查询入参(profileId、authType、language、page、pageSize、objectSearchParam、authValue)
     * @return 分页绑定对象集合(含名称、编码、权限值集合)
     */
    @PostMapping("findBindObjects")
    @Operation(summary = "分页查询配置文件已绑定的角色/用户(配置文件维度，含权限值，支持对象模糊与权限值过滤)")
    public PaginationGrid<ProfileBindObjectVO> findBindObjects(@RequestBody ProfileBindObjectQueryDTO query) {
        if (ObjectUtils.isEmpty(query) || ObjectUtils.isEmpty(query.getProfileId())) {
            throw new ParamValidateException("profileId不能为空!");
        }
        if (ObjectUtils.isEmpty(query.getAuthType())) {
            throw new ParamValidateException("authType不能为空!");
        }
        if (!DataAuthTypeEnum.contains(query.getAuthType())) {
            throw new ParamValidateException("authType参数错误");
        }
        if (ObjectUtils.isEmpty(query.getLanguage())) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(query.getLanguage())) {
            throw new ParamValidateException("language参数错误");
        }
        return dataAuthService.findBindObjects(query);
    }

    /**
     * 保存配置文件绑定的角色/用户(配置文件维度，增量语义：仅处理前端传递的对象)
     * 前端按对象维度传递需要处理的对象集合(每项=对象ID+该对象拥有的权限值)，后端仅对 bindList 中出现的对象
     * 做处理，不删除前端未传递的对象绑定。对每个对象的权限值按 authValue 唯一键做多增少删：
     * 新对象且带权限值时插入主表+权限值，已存在对象做权限值增量增删，前端传空权限值的已存在对象会被
     * 级联删除主表记录及权限值，避免出现有数据权限但权限值为空的脏数据。
     *
     * @param dto 绑定入参
     */
    @AutoIdempotent
    @PostMapping("saveBindObjects")
    @Operation(summary = "保存配置文件绑定的角色/用户及权限值(配置文件维度，增量语义，仅处理传入对象)")
    public void saveBindObjects(@RequestBody ProfileBindObjectDTO dto) {
        if (ObjectUtils.isEmpty(dto)) {
            throw new ParamValidateException("参数错误");
        }
        //获取当前用户作为创建人
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        long createBy = userResultData.getResult().getUserId();
        dataAuthService.saveBindObjects(dto, createBy);
    }



}
