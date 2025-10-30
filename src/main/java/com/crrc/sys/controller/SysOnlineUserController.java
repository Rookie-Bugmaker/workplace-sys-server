package com.crrc.sys.controller;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.service.SysOrgPersonService;
import com.crrc.sys.service.SysUserService;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线用户控制器
 *
 * @author 谭永强
 * @date 2025-10-30
 */
@RequestMapping("/user/online/")
@RestController
public class SysOnlineUserController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SysUserService userService;
    @Resource
    private SysOrgPersonService personService;

    /**
     * 分页查询在线用户
     *
     * @param param 查询条件
     * @return 在线用户列表
     */
    @PostMapping("page")
    public PaginationGrid<SysOnlineUserVO> page(@RequestBody SysOnlineUserParamDTO param) {
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize())) {
            throw new ParamValidateException("pageSize不能为空");
        }
        //查询所有在线的用户
        String clientType = param.getClientType();
        //缓存用户的key前缀
        String usernamePrefixKey = "uname_to_access:" + clientType + ":*";
        Set<String> scanKeys = scanKeys(usernamePrefixKey);
        PaginationGrid<SysOnlineUserVO> paginationGrid = new PaginationGrid<>(param.getPage(), param.getPageSize());
        if (CollectionUtils.isEmpty(scanKeys)) {
            paginationGrid.setTotal(0L);
            paginationGrid.setRows(new ArrayList<>());
            return paginationGrid;
        }
        //总数
        int count = scanKeys.size();
        int page = param.getPage();
        int pageSize = param.getPageSize();
        if (pageSize <= 0) {
            pageSize = count;
        }
        //获取当前页数据
        List<String> keyList = scanKeys.stream().skip((long) (page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        //用户名集合
        List<String> userNameList = keyList.stream().map(o -> o.substring(usernamePrefixKey.lastIndexOf(":") + 1)).collect(Collectors.toList());
        //查询用户信息
        List<SysUser> userList = userService.findByUserNameList(userNameList);
        //人员ID集合
        List<Long> personIdList = userList.stream().map(SysUser::getUserRelationId).collect(Collectors.toList());
        List<SysOrgPerson> personList = null;
        if (!CollectionUtils.isEmpty(personIdList)) {
            personList = personService.findByIds(personIdList);
        }
        List<SysOnlineUserVO> onlineUserList = new ArrayList<>();
        for (SysUser sysUser : userList) {
            SysOnlineUserVO onlineUser = new SysOnlineUserVO();
            onlineUser.setUserId(sysUser.getUserId());
            onlineUser.setUserName(sysUser.getUserName());
            onlineUser.setClientType(param.getClientType());
            onlineUser.setOnlineStatus("Y");
            if (!CollectionUtils.isEmpty(personList)) {
                SysOrgPerson person = personList.stream().filter(o -> o.getPersonId().equals(sysUser.getUserRelationId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(person)) {
                    onlineUser.setFullName(person.getFullName());
                }
            }
            onlineUserList.add(onlineUser);
        }
        paginationGrid.setTotal((long) count);
        paginationGrid.setRows(onlineUserList);
        return paginationGrid;
    }

    /**
     * 匹配缓存key
     *
     * @param pattern 模糊匹配
     * @return 结果
     */
    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (ObjectUtils.isEmpty(connectionFactory)) {
            throw new BizException("Redis客户端连接失败!");
        }
        RedisConnection connection = connectionFactory.getConnection();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                //模糊匹配
                .match(pattern)
                //每次扫描的数量
                .count(100)
                .build();
        Cursor<byte[]> scan = connection.scan(scanOptions);
        while (scan.hasNext()) {
            keys.add(new String(scan.next()));
        }
        return keys;
    }
}
