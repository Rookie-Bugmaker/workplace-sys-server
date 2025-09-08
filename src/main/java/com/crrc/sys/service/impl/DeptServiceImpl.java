package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.mapper.DeptMapper;
import com.crrc.sys.model.dept.DeptListRespVO;
import com.crrc.sys.model.dept.DeptOfUserDTO;
import com.crrc.sys.service.DeptService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {
    @Autowired
    DeptMapper deptMapper;
    /*@Autowired
    UserDeptMapper userDeptMapper;*/

    public List<Long> getSubDeptId(Long deptId) {
        List<Long> result = new ArrayList<>();
        if (deptId == null) {
            return result;
        }
        Dept dept = deptMapper.selectById(deptId);
        if (Objects.isNull(dept)) {
            return result;
        }

        // 使用队列来存储待查询的部门ID
        Queue<String> queue = new LinkedList<>();
        queue.offer(dept.getOrgCode());

        while (!queue.isEmpty()) {
            String currentOrgCode = queue.poll();

            // 查询当前部门的子部门
            LambdaQueryWrapper<Dept> wrapper = Wrappers.lambdaQuery(Dept.class)
                    .select(Dept::getId, Dept::getOrgCode)
                    .eq(Dept::getDeleted, 0)
                    .eq(Dept::getParentOrgCode, currentOrgCode);

            List<Dept> subDepts = deptMapper.selectList(wrapper);
            // 将子部门的ID添加到结果列表中，并将子部门的ID加入队列以便继续查询下一级子部门
            for (Dept subDept : subDepts) {
                result.add(subDept.getId());
                if (Strings.isEmpty(subDept.getOrgCode())) {
                    continue;
                }
                queue.offer(subDept.getOrgCode());
            }
        }

        return result;
    }

    public List<DeptOfUserDTO> getDeptByUserIdList(List<Long> userIds) {
        /*MPJLambdaWrapper<UserDept> wrapper = JoinWrappers.lambda(UserDept.class)
                .distinct()
                .select(UserDept::getUserId)
                .selectAll(Dept.class)
                .innerJoin(
                        Dept.class,
                        fn -> fn.eq(Dept::getId, UserDept::getDepartmentId)
                            .eq(Dept::getDeleted, 0)
                )
                .in(UserDept::getUserId, userIds);
        return userDeptMapper.selectJoinList(DeptOfUserDTO.class, wrapper);*/
        return null;
    }

    public List<Dept> getDeptListByCompanyId(Long companyId) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getCompanyId, companyId);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public Dept getDeptByOrgCode(String orgCode) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getOrgCode, orgCode);
        Dept dept = deptMapper.selectOne(wrapper);
        return dept;
    }

    @Override
    public List<Dept> getDeptAndSubDeptsByOrgCode(String orgCode) {
        List<Dept> result = new ArrayList<>();
        if (Strings.isEmpty(orgCode)) {
            return result;
        }

        // 获取起始部门
        Dept rootDept = getDeptByOrgCode(orgCode);
        if (Objects.isNull(rootDept)) {
            return result;
        }
        result.add(rootDept);

        // 使用队列进行广度优先搜索
        Queue<String> queue = new LinkedList<>();
        queue.offer(orgCode);

        while (!queue.isEmpty()) {
            String currentOrgCode = queue.poll();

            // 查询当前部门的子部门
            LambdaQueryWrapper<Dept> wrapper = Wrappers.lambdaQuery(Dept.class)
                    .eq(Dept::getDeleted, 0)
                    .eq(Dept::getParentOrgCode, currentOrgCode);

            List<Dept> subDepts = deptMapper.selectList(wrapper);

            // 将子部门添加到结果列表，并将子部门的orgCode加入队列以便继续查询下一级子部门
            for (Dept subDept : subDepts) {
                result.add(subDept);
                if (!Strings.isEmpty(subDept.getOrgCode())) {
                    queue.offer(subDept.getOrgCode());
                }
            }
        }

        return result;
    }


    public List<DeptListRespVO> getDeptListResp(List<Dept> deptList) {
        List<Dept> rootDepts;
        if (deptList.size() > 1) {
            rootDepts = deptList.stream()
                    .filter(dept -> Strings.isEmpty(dept.getParentOrgCode()))
                    .collect(Collectors.toList());
        } else {
            rootDepts = deptList;
        }

        List<DeptListRespVO> deptListRespVOs = BeanUtils.toBean(rootDepts, DeptListRespVO.class);
        Map<String, List<DeptListRespVO>> deptMep = deptList.stream()
                .filter(dept -> !Objects.isNull(dept.getParentOrgCode()))
                .map(dept -> BeanUtils.toBean(dept, DeptListRespVO.class))
                .collect(Collectors.groupingBy(DeptListRespVO::getParentOrgCode, Collectors.toList()));

        for (DeptListRespVO rootDept : deptListRespVOs) {
            processDepts(rootDept, deptMep);
        }
        deptListRespVOs.sort(Comparator.comparingInt(DeptListRespVO::getDepartmentOrder));
        return deptListRespVOs;
    }

    private void processDepts(DeptListRespVO rootDept, Map<String, List<DeptListRespVO>> deptMap) {
        if (!Objects.isNull(deptMap.get(rootDept.getOrgCode()))) {
            rootDept.setChildren(deptMap.get(rootDept.getOrgCode()));
        }

        if (!rootDept.getChildren().isEmpty()) {
            for (DeptListRespVO childMenus : rootDept.getChildren()) {
                processDepts(childMenus, deptMap);
            }
        }
        rootDept.getChildren().sort(Comparator.comparingInt(DeptListRespVO::getDepartmentOrder));

    }

    /**
     * 根据部门ID查询部门信息
     *
     * @param deptIdList 部门ID集合
     * @return 部门信息
     */
    @Override
    public List<Dept> findByDeptIds(List<Long> deptIdList) {
        if (CollectionUtils.isEmpty(deptIdList)) {
            return null;
        }
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getDeleted, 0);
        queryWrapper.eq(Dept::getStatus, 0);
        queryWrapper.in(Dept::getId, deptIdList);
        return super.list(queryWrapper);
    }
}
