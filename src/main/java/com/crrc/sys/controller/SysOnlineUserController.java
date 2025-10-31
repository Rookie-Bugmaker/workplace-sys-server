package com.crrc.sys.controller;

import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.Arith;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
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
    private AuthClient authClient;
    @Resource
    private Executor executor;

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
        paginationGrid.setTotal(0L);
        paginationGrid.setRows(new ArrayList<>());
        if (CollectionUtils.isEmpty(scanKeys)) {
            return paginationGrid;
        }
        int page = param.getPage();
        int pageSize = param.getPageSize();
        //用户名集合
        List<String> userNameList = scanKeys.stream().map(o -> o.substring(usernamePrefixKey.lastIndexOf(":") + 1)).collect(Collectors.toList());
        List<SysOnlineUserVO> allOnlineList = this.findAllByOnline(userNameList, param);
        if (CollectionUtils.isEmpty(allOnlineList)) {
            return paginationGrid;
        }
        List<SysOnlineUserVO> onlineUserList;
        if (pageSize > 0) {
            //pageSize<=0则只返回当前页数据
            onlineUserList = allOnlineList.stream().skip((long) (page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        } else {
            //pageSize<=0则返回所有数据
            onlineUserList = allOnlineList;
        }
        for (SysOnlineUserVO onlineUser : onlineUserList) {
            onlineUser.setClientType(param.getClientType());
            onlineUser.setOnlineStatus("Y");
        }
        paginationGrid.setTotal((long) allOnlineList.size());
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

    /**
     * 分页查询所有在线用户信息
     *
     * @param userNameList 在线用户名
     * @param param        查询条件
     * @return 结果
     */
    private List<SysOnlineUserVO> findAllByOnline(List<String> userNameList, SysOnlineUserParamDTO param) {
        //数据总数
        int count = userNameList.size();
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 500;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysOnlineUserVO> allOnlineUserList = new CopyOnWriteArrayList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        for (int i = 1; i <= page; i++) {
            //获取当前页用户列表
            final List<String> keyList = userNameList.stream().skip((long) (page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //根据用户名集合查询用户信息
                List<SysOnlineUserVO> userList = userService.findByUserNamesAndFuzzy(keyList, param);
                if (!CollectionUtils.isEmpty(userList)) {
                    allOnlineUserList.addAll(userList);
                }
            }, executor);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        return allOnlineUserList;
    }

    /**
     * 用户下线
     *
     * @param userName 用户名
     * @return 结果
     */
    @PostMapping("offline")
    public void offine(String userName) {
        if (ObjectUtils.isEmpty(userName)) {
            throw new ParamValidateException("用户名不能为空!");
        }
        ResultData<Void> resultData = authClient.clear(userName);
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getMessage());
        }
    }
}
