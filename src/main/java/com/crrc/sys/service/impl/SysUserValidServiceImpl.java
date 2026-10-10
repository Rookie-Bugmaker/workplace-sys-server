package com.crrc.sys.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.*;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.entity.user.UserExcelEntity;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.enums.*;
import com.crrc.sys.mapper.mapperImpl.*;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.service.SysOrgPersonService;
import com.crrc.sys.service.SysRoleService;
import com.crrc.sys.service.SysUserService;
import com.crrc.sys.service.SysUserValidService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 用户-接口实现
 *
 * @author 谭永强
 * @date 2022-09-02
 */
@Service
public class SysUserValidServiceImpl implements SysUserValidService {

    /**
     * 密码强度
     * 1:弱、2：中、3：强、4：非常强
     */
    public static final int PWD_STRONG = 3;
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysUserService userService;
    @Resource
    private SysOrgPersonService personService;

    @Resource
    private AuthClient authClient;

    /**
     * 校验导入人员信息并转换数据
     *
     * @param list 待校验数据
     * @return 转换后数据列表
     */
    @Override
    public List<SysUser> validateBuild(List<UserExcelEntity> list) {
        // 1. 数据准备阶段
        List<String> userNames = extractUserNames(list);
        List<String> repeatNames = findDuplicateNames(userNames);
        List<SysRole> roleList = getRoleList(list);
        List<String> roleCodeList = extractRoleCodes(roleList);

        // 2. 批量查询阶段
        List<String> personNos = extractPersonNos(list);
        Set<String> userNameSet = new HashSet<>(userNames);
        List<SysUser> existsUsers = batchQueryUsers(userNameSet);
        List<SysOrgPerson> relationUsers = batchQueryPersons(personNos);

        // 3. 用户数据构建和验证阶段
        SysUser currentUser = getCurrentUser();
        List<String> existsUserNames = existsUsers.stream().map(SysUser::getUserName).collect(Collectors.toList());

        return buildAndValidateUsers(list, repeatNames, existsUserNames, roleList, roleCodeList, relationUsers, currentUser);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param user      用户信息
     */
    @Override
    public void verification(String operation, SysUser user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(user.getUserName())) {
            throw new ParamValidateException("用户名不能为空!");
        }
        if (ObjectUtils.isEmpty(user.getNextLoginUpdatePassword())) {
            user.setNextLoginUpdatePassword("N");
        }
        if (!"Y".equals(user.getNextLoginUpdatePassword()) && !"N".equals(user.getNextLoginUpdatePassword())) {
            throw new ParamValidateException("nextLoginUpdatePassword参数错误(Y/N):" + user.getNextLoginUpdatePassword());
        }
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            SysUser sysUserResultData = userService.findByUserName(user.getUserName());
            if (!ObjectUtils.isEmpty(sysUserResultData)) {
                throw new ParamValidateException("用户名已存在!");
            }
            if (ObjectUtils.isEmpty(user.getPassword())) {
                throw new ParamValidateException("密码不能为空!");
            }
            if (user.getPassword().length() < 8) {
                throw new ParamValidateException("密码长度不能低于8位!");
            }
            //密码强度等级
            int strong = checkStrong(user.getPassword(), user.getUserName(), user.getUserDescription());
            if (strong < PWD_STRONG) {
                throw new ParamValidateException("密码强度太低!");
            }
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(user.getUserId())) {
                throw new ParamValidateException("用户ID不能为空!");
            }
            SysUser sysUser = userService.findByUserName(user.getUserName());
            if (!ObjectUtils.isEmpty(sysUser) && !sysUser.getUserId().equals(user.getUserId())) {
                throw new ParamValidateException("用户名已存在!");
            }
            if (!ObjectUtils.isEmpty(user.getPassword())) {
                if (checkStrong(user.getPassword(), user.getUserName(), user.getUserDescription()) < PWD_STRONG) {
                    throw new ParamValidateException("密码强度太低!");
                }
            }
        }
        if (ObjectUtils.isEmpty(user.getUserType())) {
            throw new ParamValidateException("用户类型不能为空!");
        }
        if (ObjectUtils.isEmpty(user.getDisableState())) {
            throw new ParamValidateException("状态不能为空!");
        }
        if (!EnableFlagEnum.contains(user.getDisableState())) {
            throw new ParamValidateException("状态错误!");
        }
        if (CollectionUtils.isEmpty(user.getRoleIds())) {
            throw new ParamValidateException("请选择角色!");
        }
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!ObjectUtils.isEmpty(dataAuthList)) {
            if (!CollectionUtils.isEmpty(dataAuthList)) {
                for (SysDataAuth dataAuth : dataAuthList) {
                    List<SysDataAuthValue> authValueList = dataAuth.getDataAuthValueList();
                    if (CollectionUtils.isEmpty(authValueList)) {
                        throw new ParamValidateException("用户权限值不能为空!");
                    }
                }
            }
        }
        //校验一个人员只能绑定一个用户
        if (UserTypeEnum.EMPLOYEE.getValue().equals(user.getUserType()) && !ObjectUtils.isEmpty(user.getUserRelationId())) {
            //根据人员ID查询用户
            List<SysUser> userList = userService.findByRelationIds(Collections.singletonList(user.getUserRelationId()));
            if (!CollectionUtils.isEmpty(userList)) {
                if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                    throw new BizException("该人员已绑定用户");
                }
                if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !userList.get(0).getUserId().equals(user.getUserId())) {
                    throw new BizException("该人员已绑定用户");
                }
            }
        }
    }

    /**
     * 1.密码组成字符包括以下四类:数字、大写字母、小写字母、特殊字符，必须包含三种字符及以上；
     * 2.密码长度为8至16位，密码不能包含账号。
     * 3.连续3位及以上数字不能连号(例如123、654)
     * 4.连续3位及以上字母不能连续(例如abc、cba)
     * 5.密码不能包含连续3个及以上相同字符(例如aaa、rrr)
     * 6.密码不能包含用户名、姓名大小写全拼
     *
     * @param pwd      密码
     * @param userName 用户名
     * @return 1:弱、2：中、3：强、4：非常强
     */
    @Override
    public int checkStrong(String pwd, String userName, String fullName) {
        int flag = 0;
        if (ObjectUtils.isEmpty(pwd)) {
            return flag;
        }
        if (pwd.length() < 8) {
            throw new ParamValidateException("密码长度不能小于8位!");
        }
        if (pwd.length() > 16) {
            throw new ParamValidateException("密码长度不能大于16位!");
        }
        if (pwd.contains(userName)) {
            throw new ParamValidateException("密码不能包含账号!");
        }
        if (!ObjectUtils.isEmpty(fullName) && pwd.toLowerCase().contains(ChineseToPinyin.convertToHanYuPinyin(fullName).toLowerCase())) {
            throw new ParamValidateException("密码不能包含用户姓名大小写全拼!");
        }
        if (MyUtils.isContinuousDigits(pwd)) {
            throw new ParamValidateException("连续3位及以上(数字/字母)不能连号!");
        }
        if (pwd.matches(".*[0-9].*")) {
            //数字
            flag++;
        }
        if (pwd.matches(".*[a-z].*")) {
            //小写字母
            flag++;
        }
        if (pwd.matches(".*[A-Z].*")) {
            //大写字母
            flag++;
        }
        if (pwd.matches(".*[!@*#$\\-_()+=&￥.].*")) {
            //特殊字符
            flag++;
        }
        return flag;
    }

    private boolean containsChinese(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提取用户名列表
     */
    private List<String> extractUserNames(List<UserExcelEntity> list) {
        return list.stream()
                .map(UserExcelEntity::getUserName)
                .filter(org.springframework.util.StringUtils::hasText)
                .collect(Collectors.toList());
    }

    /**
     * 查找重复用户名
     */
    private List<String> findDuplicateNames(List<String> userNames) {
        return userNames.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取角色列表
     */
    private List<SysRole> getRoleList(List<UserExcelEntity> list) {
        List<String> roleCodes = list.stream()
                .map(UserExcelEntity::getRoleAuth)
                .filter(org.springframework.util.StringUtils::hasText)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(roleCodes)) {
            return new ArrayList<>();
        }

        Set<String> roleCodeList = new HashSet<>();
        roleCodes.forEach(roleCode -> {
            List<String> split = Arrays.asList(roleCode.split(","));
            roleCodeList.addAll(split);
        });

        return roleService.findByCodes(new ArrayList<>(roleCodeList), LanguageEnum.ZH_CN.getValue());
    }

    /**
     * 提取角色编码列表
     */
    private List<String> extractRoleCodes(List<SysRole> roleList) {
        return roleList.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 提取关联人员编号
     */
    private List<String> extractPersonNos(List<UserExcelEntity> list) {
        return list.stream()
                .map(UserExcelEntity::getUserRelation)
                .filter(org.springframework.util.StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 批量查询用户
     */
    private List<SysUser> batchQueryUsers(Set<String> userNameSet) {
        return batchQuery(userNameSet,
                userService::findByUserNameList,
                new ArrayList<>());
    }

    /**
     * 批量查询人员
     */
    private List<SysOrgPerson> batchQueryPersons(List<String> personNos) {
        return batchQuery(new HashSet<>(personNos),
                personService::findByJobNos,
                new ArrayList<>());
    }

    /**
     * 通用批量查询方法
     */
    private <T, R> List<R> batchQuery(Set<T> querySet, Function<List<T>, List<R>> queryFunction, List<R> resultList) {
        if (CollectionUtils.isEmpty(querySet)) {
            return resultList;
        }

        final int batchSize = 900;
        int totalCount = querySet.size();
        int batchCount = (totalCount + batchSize - 1) / batchSize;

        ThreadPoolExecutor executor = MyThreadPoolExecutor.getThreadPoolExecutor(batchCount);
        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);

        try {
            List<CompletableFuture<Void>> futures = IntStream.range(0, batchCount)
                    .mapToObj(i -> {
                        int start = i * batchSize;
                        int end = Math.min(start + batchSize, totalCount);
                        List<T> batchItems = new ArrayList<>(querySet).subList(start, end);

                        return CompletableFuture.runAsync(() -> {
                            List<R> batchResult = queryFunction.apply(batchItems);
                            synchronized (resultList) {
                                resultList.addAll(batchResult);
                            }
                        }, executor);
                    })
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } finally {
            executor.shutdown();
        }

        return resultList;
    }

    /**
     * 获取当前用户
     */
    private SysUser getCurrentUser() {
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        return userResultData.getResult();
    }

    /**
     * 构建并验证用户数据（多线程优化版）
     */
    private List<SysUser> buildAndValidateUsers(List<UserExcelEntity> list,
                                                List<String> repeatNames,
                                                List<String> existsUserNames,
                                                List<SysRole> roleList,
                                                List<String> roleCodeList,
                                                List<SysOrgPerson> relationUsers,
                                                SysUser currentUser) {
        // 1. 初始化线程安全的工具类
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        Date nowDate = new Date();

        // 2. 并行校验并保留原始行序，重复用户名按首行创建用户
        return list.parallelStream().map(excelEntity -> {
            // 每个线程使用独立的StringBuilder避免线程安全问题
            StringBuilder validateMsg = new StringBuilder();

            // 3. 构建用户信息
            SysUser user = buildUser(excelEntity, currentUser, nowDate, bcpe);

            // 4. 验证用户数据
            validateUser(excelEntity, user, validateMsg,
                    roleList, roleCodeList, relationUsers, currentUser);

            // 5. 重复用户只作为报告提示，不影响其他字段的校验结果
            excelEntity.setValidateStatus(validateMsg.length() > 0 ? StatusEnum.N.name() : StatusEnum.Y.name());
            if (existsUserNames.contains(excelEntity.getUserName())) {
                validateMsg.append("【用户名已存在，跳过用户新增，仅增量添加角色，重复角色跳过】");
            }
            if (repeatNames.contains(excelEntity.getUserName())) {
                validateMsg.append("【文件内用户名重复，用户仅保留一条，角色合并去重】");
            }
            excelEntity.setValidateMsg(validateMsg.toString());

            return user;
        }).collect(Collectors.toList());
    }

    /**
     * 构建用户基础信息
     */
    private SysUser buildUser(UserExcelEntity excelEntity, SysUser currentUser, Date nowDate, PasswordEncoder bcpe) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(excelEntity, user);

        user.setPassword(bcpe.encode(user.getPassword()));
        user.setCreateBy(currentUser.getUserId());
        user.setCreateDate(nowDate);
        user.setUserId(idKeyGenerator.getId());

        return user;
    }

    /**
     * 验证用户数据
     */
    private void validateUser(UserExcelEntity excelEntity,
                              SysUser user,
                              StringBuilder validateMsg,
                              List<SysRole> roleList,
                              List<String> roleCodeList,
                              List<SysOrgPerson> relationUsers,
                              SysUser currentUser) {
        validateUserName(excelEntity, validateMsg);
        validatePassword(excelEntity, validateMsg, currentUser);
        validateUserType(excelEntity, user, validateMsg);
        validateRelationUser(excelEntity, user, validateMsg, relationUsers);
        validateUserStatus(excelEntity, user, validateMsg);
        validateUserDescription(excelEntity, validateMsg);
        validateRoleAuth(excelEntity, user, validateMsg, roleList, roleCodeList);
    }

    /**
     * 验证用户名
     */
    private void validateUserName(UserExcelEntity excelEntity, StringBuilder validateMsg) {
        if (ObjectUtils.isEmpty(excelEntity.getUserName())) {
            validateMsg.append("【用户名为空】");
        } else {
            if (containsChinese(excelEntity.getUserName())) {
                validateMsg.append("【用户名不能包含中文】");
            }
        }
    }

    /**
     * 验证密码
     */
    private void validatePassword(UserExcelEntity excelEntity, StringBuilder validateMsg, SysUser currentUser) {
        if (ObjectUtils.isEmpty(excelEntity.getPassword())) {
            validateMsg.append("【密码为空】");
        } else if (checkStrong(excelEntity.getPassword(), currentUser.getUserName(), null) < PWD_STRONG) {
            validateMsg.append("【密码强度太低】");
        }
    }

    /**
     * 验证用户类型
     */
    private void validateUserType(UserExcelEntity excelEntity, SysUser user, StringBuilder validateMsg) {
        if (ObjectUtils.isEmpty(excelEntity.getUserType())) {
            validateMsg.append("【用户类型为空】");
        } else {
            UserTypeEnum userTypeEnum = Arrays.stream(UserTypeEnum.values())
                    .filter(x -> x.getText().equals(excelEntity.getUserType()))
                    .findFirst()
                    .orElse(null);

            if (ObjectUtils.isEmpty(userTypeEnum)) {
                validateMsg.append("【用户类型验证失败】");
            } else {
                user.setUserType(userTypeEnum.getValue());
            }
        }
    }

    /**
     * 验证关联用户
     */
    private void validateRelationUser(UserExcelEntity excelEntity,
                                      SysUser user,
                                      StringBuilder validateMsg,
                                      List<SysOrgPerson> relationUsers) {
        if (org.springframework.util.StringUtils.hasText(excelEntity.getUserRelation())) {
            SysOrgPerson person = relationUsers.stream()
                    .filter(x -> x.getJobNo().equals(excelEntity.getUserRelation()))
                    .findFirst()
                    .orElse(null);

            if (ObjectUtils.isEmpty(person)) {
                validateMsg.append("【关联用户验证失败】");
            } else {
                user.setUserRelationId(person.getPersonId());
            }
        }
    }

    /**
     * 验证用户状态
     */
    private void validateUserStatus(UserExcelEntity excelEntity, SysUser user, StringBuilder validateMsg) {
        if (ObjectUtils.isEmpty(excelEntity.getDisableState())) {
            validateMsg.append("【用户状态为空】");
        } else {
            UserStatusEnum userStatusEnum = Arrays.stream(UserStatusEnum.values())
                    .filter(x -> x.getText().equals(excelEntity.getDisableState()))
                    .findFirst()
                    .orElse(null);

            if (ObjectUtils.isEmpty(userStatusEnum)) {
                validateMsg.append("【用户状态验证失败】");
            } else {
                user.setDisableState(userStatusEnum.getValue());
            }
        }
    }

    /**
     * 验证用户描述
     */
    private void validateUserDescription(UserExcelEntity excelEntity, StringBuilder validateMsg) {
        if (ObjectUtils.isEmpty(excelEntity.getUserDescription())) {
            validateMsg.append("【用户描述为空】");
        } else if (excelEntity.getUserDescription().length() > 50) {
            validateMsg.append("【用户描述过长】");
        }
    }

    /**
     * 验证角色授权
     */
    private void validateRoleAuth(UserExcelEntity excelEntity,
                                  SysUser user,
                                  StringBuilder validateMsg,
                                  List<SysRole> roleList,
                                  List<String> roleCodeList) {
        if (ObjectUtils.isEmpty(excelEntity.getRoleAuth())) {
            validateMsg.append("【角色授权为空】");
            return;
        }

        List<String> currentRoleCodes = Arrays.asList(excelEntity.getRoleAuth().split(","));
        if (!roleCodeList.containsAll(currentRoleCodes)) {
            validateMsg.append("【角色验证失败】");
            return;
        }

        List<SysUserRole> currentRoleList = new ArrayList<>();
        currentRoleCodes.forEach(roleCode -> {
            SysRole sysRole = roleList.stream()
                    .filter(x -> x.getRoleCode().equals(roleCode))
                    .findFirst()
                    .orElse(new SysRole());

            SysUserRole userRole = new SysUserRole();
            userRole.setId(idKeyGenerator.getId());
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(sysRole.getRoleId());
            userRole.setCreateBy(user.getCreateBy());
            userRole.setCreateDate(user.getCreateDate());

            currentRoleList.add(userRole);
        });

        user.setUserRoleList(currentRoleList);
    }

//    /**
//     * 校验导入人员信息并转换数据
//     *
//     * @param list 待校验数据
//     * @return 转换后数据列表
//     */
//    @Override
//    public List<SysUser> validateBuild(List<UserExcelEntity> list) {
//        StringBuilder validateMsg = new StringBuilder();
//        List<String> userNames = list.stream().map(UserExcelEntity::getUserName).filter(org.springframework.util.StringUtils::hasText).collect(Collectors.toList());
//        // 获取重复用户名
//        List<String> repeatNames = userNames.stream().collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
//                .entrySet().stream()
//                .filter(entry -> entry.getValue() > 1)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//        // 获取分配角色
//        List<String> roleCodes = list.stream().map(UserExcelEntity::getRoleAuth).filter(org.springframework.util.StringUtils::hasText).collect(Collectors.toList());
//        List<SysRole> roleList = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(roleCodes)) {
//            Set<String> roleCodeList = new HashSet<>();
//            roleCodes.forEach(roleCode -> {
//                List<String> split = Arrays.asList(roleCode.split(","));
//                roleCodeList.addAll(split);
//            });
//            // 获取角色信息
//            List<SysRole> roleResult = roleService.findByCodes(new ArrayList<>(roleCodeList), LanguageEnum.ZH_CN.getValue());
//            roleList.addAll(roleResult);
//        }
//        List<String> roleCodeList = roleList.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
//        // 存放关联用户
//        List<String> personNos = list.stream().map(UserExcelEntity::getUserRelation).filter(org.springframework.util.StringUtils::hasText).distinct().collect(Collectors.toList());
//        List<SysOrgPerson> relationUsers = new ArrayList<>();
//        // 存放数据库已存在用户
//        List<SysUser> existsUsers = new ArrayList<>();
//        // 每次查询900条
//        int constantQuantity = 900;
//        // 去重减少查询次数
//        Set<String> userNameSet = new HashSet<>(userNames);
//        // 设置共享request
//        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
//        // 计算查询次数
//        int userNameCount = (userNameSet.size() / constantQuantity);
//        if (userNameSet.size() % constantQuantity != 0) {
//            userNameCount += 1;
//        }
//        int personNoCount = (personNos.size() / constantQuantity);
//        if (personNos.size() % constantQuantity != 0) {
//            personNoCount += 1;
//        }
//        // 获取最大的查询次数
//        int maxCount = Stream.of(personNoCount, userNameCount).max(Integer::compare).get();
//        String[] mapKeys = {"user", "person"};
//        int finalUserNameCount = userNameCount;
//        int finalPersonNoCount = personNoCount;
//        //自定义创建线程池对象
//        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(maxCount);
//        List<CompletableFuture<Boolean>> futures = IntStream.rangeClosed(1, maxCount)
//                //构建查询参数
//                .mapToObj(i -> {
//                    Map<String, List<String>> map = new HashMap<>();
//                    if (finalUserNameCount <= i) {
//                        int startIndex = (i - 1) * constantQuantity;
//                        int endIndex = i * constantQuantity;
//                        if (finalUserNameCount == i) {
//                            endIndex = userNameSet.size();
//                        }
//                        map.put(mapKeys[0], new ArrayList<>(userNameSet).subList(startIndex, endIndex));
//                    }
//                    if (finalPersonNoCount <= i) {
//                        int startIndex = (i - 1) * constantQuantity;
//                        int endIndex = i * constantQuantity;
//                        if (finalPersonNoCount == i || CollUtil.isEmpty(personNos)) {
//                            endIndex = personNos.size();
//                        }
//                        map.put(mapKeys[1], new ArrayList<>(personNos).subList(startIndex, endIndex));
//                    }
//                    return map;
//                })
//                //构建异步线程
//                .map(map -> CompletableFuture.supplyAsync(() -> {
//                    // 查询用户
//                    if (map.containsKey(mapKeys[0])) {
//                        List<SysUser> byUserNameList = this.findByUserNameList(map.get(mapKeys[0]));
//                        synchronized (existsUsers) {
//                            existsUsers.addAll(byUserNameList);
//                        }
//                    }
//                    // 查询人员
//                    if (map.containsKey(mapKeys[1]) && CollUtil.isNotEmpty(map.get(mapKeys[1]))) {
//                        List<SysOrgPerson> byJobNos = personService.findByJobNos(map.get(mapKeys[1]));
//                        synchronized (relationUsers) {
//                            relationUsers.addAll(byJobNos);
//                        }
//                    }
//                    return true;
//                }, myThreadPool).exceptionally(e -> {
//                    e.getCause().printStackTrace();
//                    return false;
//                }))
//                .collect(Collectors.toList());
//        //等待线程全部处理完毕
//        List<Boolean> resultList = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[]{}))
//                .thenApply(f -> futures.stream()
//                        .map(CompletableFuture::join)
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList()))
//                .join();
//        myThreadPool.shutdown();
//        //判断是否存在异常
//        Boolean threadResult = resultList.stream().filter(o -> !o).findFirst().orElse(true);
//        if (Boolean.FALSE.equals(threadResult)) {
//            //抛出异常
//            throw new BizException(ReturnCode.RC500.getCode(), "查询失败,请重试!");
//        }
//        // 数据库已存在的用户名
//        List<String> existsUserNames = existsUsers.stream().map(SysUser::getUserName).collect(Collectors.toList());
//        //获取当前用户
//        ResultData<SysUser> userResultData = authClient.currentUser();
//        if (Boolean.FALSE.equals(userResultData.getSuccess())) {
//            throw new BizException(userResultData.getCode(), userResultData.getMessage());
//        }
//        //当前用户
//        SysUser currentUser = userResultData.getResult();
//        PasswordEncoder bcpe = new BCryptPasswordEncoder();
//        Date nowDate = new Date();
//        List<SysUser> result = new ArrayList<>();
//        list.forEach(excelEntity -> {
//            SysUser user = new SysUser();
//            BeanUtils.copyProperties(excelEntity, user);
//            // 密文
//            String enPassword = bcpe.encode(user.getPassword());
//            user.setPassword(enPassword);
//            // 设置创建人、创建时间
//            user.setCreateBy(currentUser.getUserId());
//            user.setCreateDate(nowDate);
//            // 设置Id
//            user.setUserId(idKeyGenerator.getId());
//            // 验证用户名
//            if (ObjectUtils.isEmpty(excelEntity.getUserName())) {
//                validateMsg.append("【用户名为空】");
//            } else {
//                if (repeatNames.contains(excelEntity.getUserName())) {
//                    validateMsg.append("【用户名输入重复】");
//                }
//                if (existsUserNames.contains(excelEntity.getUserName())) {
//                    validateMsg.append("【用户名已存在】");
//                }
//                if (containsChinese(excelEntity.getUserName())) {
//                    validateMsg.append("【用户名不能包含中文】");
//                }
//            }
//            // 验证密码
//            if (ObjectUtils.isEmpty(excelEntity.getPassword())) {
//                validateMsg.append("【密码为空】");
//            }
//            if (checkStrong(excelEntity.getPassword(), currentUser.getUserName(), null) < PWD_STRONG) {
//                validateMsg.append("【密码强度太低】");
//            }
//            // 验证用户类型
//            if (ObjectUtils.isEmpty(excelEntity.getUserType())) {
//                validateMsg.append("【用户类型为空】");
//            } else {
//                UserTypeEnum userTypeEnum = Arrays.stream(UserTypeEnum.values()).filter(x -> x.getText().equals(excelEntity.getUserType())).findFirst().orElse(null);
//                if (ObjectUtils.isEmpty(userTypeEnum)) {
//                    validateMsg.append("【用户类型验证失败】");
//                } else {
//                    user.setUserType(userTypeEnum.getValue());
//                }
//            }
//            // 验证关联用户
//            if (org.springframework.util.StringUtils.hasText(excelEntity.getUserRelation())) {
//                SysOrgPerson person = relationUsers.stream().filter(x -> x.getJobNo().equals(excelEntity.getUserRelation())).findFirst().orElse(null);
//                if (ObjectUtils.isEmpty(person)) {
//                    validateMsg.append("【关联用户验证失败】");
//                } else {
//                    user.setUserRelationId(person.getPersonId());
//                }
//            }
//            // 验证生效日期
////            if (ObjectUtils.isEmpty(excelEntity.getStartDate())) {
////                validateMsg.append("【生效日期为空】");
////            }
//            // 验证状态
//            if (ObjectUtils.isEmpty(excelEntity.getDisableState())) {
//                validateMsg.append("【用户状态为空】");
//            } else {
//                UserStatusEnum userStatusEnum = Arrays.stream(UserStatusEnum.values()).filter(x -> x.getText().equals(excelEntity.getDisableState())).findFirst().orElse(null);
//                if (ObjectUtils.isEmpty(userStatusEnum)) {
//                    validateMsg.append("【用户状态验证失败】");
//                } else {
//                    user.setDisableState(userStatusEnum.getValue());
//                }
//            }
//            // 验证用户描述
//            if (ObjectUtils.isEmpty(excelEntity.getUserDescription())) {
//                validateMsg.append("【用户描述为空】");
//            } else if (excelEntity.getUserDescription().length() > 50) {
//                validateMsg.append("【用户描述过长】");
//            }
//            // 验证角色授权
//            if (ObjectUtils.isEmpty(excelEntity.getRoleAuth())) {
//                validateMsg.append("【角色授权为空】");
//            } else {
//                List<String> currentRoleCodes = Arrays.asList(excelEntity.getRoleAuth().split(","));
//                if (!roleCodeList.containsAll(currentRoleCodes)) {
//                    validateMsg.append("【角色验证失败】");
//                } else {
//                    ArrayList<SysUserRole> currentRoleList = new ArrayList<>();
//                    currentRoleCodes.forEach(roleCode -> {
//                        SysRole sysRole = roleList.stream().filter(x -> x.getRoleCode().equals(roleCode)).findFirst().orElse(new SysRole());
//                        SysUserRole userRole = new SysUserRole();
//                        userRole.setId(idKeyGenerator.getId());
//                        userRole.setUserId(user.getUserId());
//                        userRole.setRoleId(sysRole.getRoleId());
//                        userRole.setCreateBy(currentUser.getUserId());
//                        userRole.setCreateDate(nowDate);
//                        currentRoleList.add(userRole);
//                    });
//                    user.setUserRoleList(currentRoleList);
//                }
//            }
//            excelEntity.setValidateStatus(StatusEnum.Y.name());
//            if (validateMsg.length() > 0) {
//                excelEntity.setValidateStatus(StatusEnum.N.name());
//                excelEntity.setValidateMsg(validateMsg.toString());
//                validateMsg.setLength(0);
//            }
//            result.add(user);
//        });
//        return result;
//    }
}
