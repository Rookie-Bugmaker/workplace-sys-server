package com.crrc.sys.controller;


import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.service.homepage.SysHomePageComponentService;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.asserts.BizServiceAssert;
import com.crrc.sys.entity.homepage.HomePageParam;
import com.crrc.sys.entity.homepage.SysHomePageComponent;
import com.crrc.sys.entity.homepage.SysHomePageComponentUserParam;
import com.crrc.sys.entity.user.SysUser;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 主页组件定义表 前端控制器
 * </p>
 *
 * @author fucheng
 * @since 2023-05-19
 */
@RestController
@RequestMapping("/homePage/component/")
public class SysHomePageComponentController {

    @Resource
    private AuthClient authClient;
    @Resource
    private SysHomePageComponentService homePageComponentService;

    /**
     * 新增主页组件
     */
    @AutoIdempotent
    @PostMapping("add")
    public Integer add(SysHomePageComponent param) {
        param.setCreateBy(getUserId());
        param.setCreateDate(new Date());
        checkData(param);
        return homePageComponentService.add(param);
    }

    /**
     * 分页查询
     *
     * @param param 查询参数
     */
    @PostMapping("pageList")
    public PaginationGrid<SysHomePageComponent> pageList(HomePageParam param) {
        return homePageComponentService.pageList(param);
    }

    /**
     * 按id删除
     *
     * @param componentId 主键id
     */
    @AutoIdempotent
    @DeleteMapping("{componentId}")
    public Integer deleteById(@PathVariable Long componentId) {
        return homePageComponentService.deleteById(componentId);
    }

    /**
     * 按id更新
     *
     * @param param 参数
     */
    @AutoIdempotent
    @PutMapping("updateById")
    public Integer updateById(SysHomePageComponent param) {
        param.setLastUpdateBy(getUserId());
        param.setLastUpdateDate(new Date());
        param.setLastUpdateLogin(getUserId());
        checkData(param);
        return homePageComponentService.updateById(param);
    }

    /**
     * 组件分配用户
     */
    @AutoIdempotent
    @PostMapping("addUser")
    public Integer addUser(SysHomePageComponentUserParam param) {
        param.setCreateBy(getUserId());
        return homePageComponentService.addUser(param);
    }

    /**
     * 查询已分配用户
     */
    @GetMapping("findUserByComponentId")
    public List<Long> findUserByComponentId(Long componentId) {
        return homePageComponentService.findUserByComponentId(componentId);
    }

    /**
     * 查询当前用户组件列表
     */
    @GetMapping("findCurrentUserComponents")
    public List<SysHomePageComponent> findCurrentUserComponents() {
        return homePageComponentService.findComponentByUserId(getUserId());
    }

    /**
     * 获取当前用户
     */
    private Long getUserId() {
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        return currentUser.getUserId();
    }

    /**
     * 校验参数
     */
    private void checkData(SysHomePageComponent param) {
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getHomePageTitle()), "主页标题不能为空！");
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getFunctionName()), "功能名称不能为空！");
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getBusinessAreaId()), "业务领域不能为空！");
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getRefreshRate()), "刷新频率不能为空！");
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getLoadingType()), "加载限制不能为空！");
        checkLength(param);
    }

    /**
     * 校验长度
     */
    private void checkLength(SysHomePageComponent param) {
        BizServiceAssert.isTrue(param.getHomePageTitle().getBytes().length < 50, "超过数据库规定长度，请重新输入");
        BizServiceAssert.isTrue(param.getFunctionName().getBytes().length < 50, "超过数据库规定长度，请重新输入");
        if (!ObjectUtils.isEmpty(param.getFunctionCode())) {
            BizServiceAssert.isTrue(param.getFunctionCode().getBytes().length < 50, "超过数据库规定长度，请重新输入");
        }
        if (!ObjectUtils.isEmpty(param.getRemarks())) {
            BizServiceAssert.isTrue(param.getRemarks().getBytes().length < 100, "超过数据库规定长度，请重新输入");
        }
    }
}
