package com.crrc.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.model.dict.DictExcelVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.model.dict.DictPageReqVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 字典 服务类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
public interface DictService extends IService<Dict> {
    PaginationGrid<Dict> page(DictPageReqVO pageReqVO);

    List<Dict> query(DictListReqVO queryReqVO);
    Dict getByCode(String dictCode);
    List<Dict> getByName(String dictName);
    List<Dict> getAll();

    void exports(DictExcelVO exportReqVO, HttpServletResponse response);

    List<DictExcelVO> reads(MultipartFile file);

    SysUser getUserRealNameInDictExtends(Long userId, String userName);
    Dept getDeptNameNameInDictExtends(Long deptId, String orgCode);
}
