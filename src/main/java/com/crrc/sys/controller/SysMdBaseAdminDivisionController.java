package com.crrc.sys.controller;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.response.ResponseResult;
import com.crrc.sys.entity.admindivision.AdminDivisionListVo;
import com.crrc.sys.entity.admindivision.AdminDivisionQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeVo;
import com.crrc.sys.service.SysMdBaseAdminDivisionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/base/adminDivision")
public class SysMdBaseAdminDivisionController {

    @Resource
    private SysMdBaseAdminDivisionService adminDivisionService;

    /**
     * 左侧树
     */
    @PostMapping("/tree")
    public ResponseResult<List<AdminDivisionTreeVo>> tree(
            @RequestBody(required = false) AdminDivisionTreeQueryVo vo) {
        return ResponseResult.success(adminDivisionService.tree(vo));
    }

    /**
     * 右侧分页列表
     */
    @PostMapping("/page")
    public PaginationGrid<AdminDivisionListVo> page(
            @RequestBody(required = false) AdminDivisionQueryVo vo) {
        return adminDivisionService.page(vo);
    }

    /**
     * 详情弹窗
     */
    @GetMapping("/detail/{divisionNumber}")
    public ResponseResult<AdminDivisionListVo> detail(
            @PathVariable String divisionNumber) {
        return ResponseResult.success(adminDivisionService.detail(divisionNumber));
    }
}