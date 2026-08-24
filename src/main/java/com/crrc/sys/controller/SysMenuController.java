package com.crrc.sys.controller;

import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.*;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.menu.SysMenu;
import com.crrc.sys.entity.menu.SysMenuDTO;
import com.crrc.sys.entity.menu.SysMenuFindByPageParam;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OperationEnum;
import com.crrc.sys.service.SysMenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-21
 */
@RestController
@RequestMapping("menu")
public class SysMenuController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysMenuService menuService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 菜单信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysMenu> findByPage(SysMenuFindByPageParam param) {
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
        return menuService.findByPage(param);
    }

    /**
     * 新增(新)
     * 此接口只会接收当前操作的菜单数据，不会处理子菜单
     *
     * @param menu 菜单信息
     */
    @AutoIdempotent
    @PostMapping("add")
    public long add(@RequestBody SysMenuDTO menu, String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        menu.setLanguage(language);
        verification(menu, OperationEnum.ADD);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        menu.setMenuId(idKeyGenerator.getId());
        //设置创建人
        menu.setCreateBy(currentUser.getUserId());
        //设置创建时间
        menu.setCreateDate(Calendar.getInstance().getTime());
        int result = menuService.add(menu);
        if (result != 1) {
            throw new BizException("新增菜单失败");
        }
        return menu.getMenuId();
    }

    /**
     * 修改(新)
     * 此接口只会接收当前操作的菜单数据，不会处理子菜单
     *
     * @param menu 菜单信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(@RequestBody SysMenuDTO menu, String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        menu.setLanguage(language);
        verification(menu, OperationEnum.UPDATE);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        menu.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        menu.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        menu.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = menuService.update(menu);
        if (result != 1) {
            throw new BizException("修改菜单失败");
        }
    }

    /**
     * 参数校验(新)
     *
     * @param menu      参数
     * @param operation 接口类型，新增/修改
     */
    private void verification(SysMenuDTO menu, OperationEnum operation) {
        if (ObjectUtils.isEmpty(menu)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationEnum.UPDATE.equals(operation) && ObjectUtils.isEmpty(menu.getMenuId())) {
            throw new ParamValidateException("菜单ID不能为空");
        }
        if (ObjectUtils.isEmpty(menu.getParentId())) {
            if (ObjectUtils.isEmpty(menu.getMenuCode())) {
                throw new ParamValidateException("菜单编码不能为空");
            }
            if (menu.getMenuCode().length() > 50) {
                throw new ParamValidateException("菜单编码超长(最大50)");
            }
            List<SysMenu> menuList = menuService.findByCodes(Collections.singletonList(menu.getMenuCode()), menu.getLanguage());
            if (!CollectionUtils.isEmpty(menuList)) {
                if (OperationEnum.ADD.equals(operation)) {
                    throw new ParamValidateException("菜单编码:[" + menu.getMenuCode() + "]已存在");
                }
                if (OperationEnum.UPDATE.equals(operation) && !menuList.get(0).getMenuId().equals(menu.getMenuId())) {
                    throw new ParamValidateException("菜单编码:[" + menu.getMenuCode() + "]已存在");
                }
            }
        } else {
            SysMenu parentMenu = menuService.findMinById(menu.getParentId(), menu.getLanguage());
            if (ObjectUtils.isEmpty(parentMenu)) {
                throw new ParamValidateException("parentId不存在!");
            }
        }
        if (ObjectUtils.isEmpty(menu.getMenuName())) {
            throw new ParamValidateException("菜单名称不能为空");
        }
        if (menu.getMenuName().length() > 50) {
            throw new ParamValidateException("菜单名称超长(最大50)");
        }
        if (!ObjectUtils.isEmpty(menu.getChildId()) && !ObjectUtils.isEmpty(menu.getFunctionId())) {
            throw new ParamValidateException("childId与functionId不能同时存在!");
        }
        if (!ObjectUtils.isEmpty(menu.getParentId()) && !ObjectUtils.isEmpty(menu.getChildId())) {
            //查询父菜单下的所有子菜单
            List<Long> menuIdList = menuService.findMenuIdList(menu.getParentId());
            //如果当前操作为'修改'操作，则需要排除原来的子菜单
            if (OperationEnum.UPDATE.equals(operation)) {
                //查询原来的菜单信息
                SysMenu oldSysMenu = menuService.findMinById(menu.getMenuId(), menu.getLanguage());
                //查询原来的菜单绑定的子菜单集合
                if (!ObjectUtils.isEmpty(oldSysMenu.getChildId())) {
                    List<Long> oldChildMenuIdList = menuService.findMenuIdList(oldSysMenu.getChildId());
                    if (!CollectionUtils.isEmpty(oldChildMenuIdList)) {
                        menuIdList.removeIf(oldChildMenuIdList::contains);
                    }
                }
            }
            //查询当前子菜单下的所有子菜单
            List<Long> childMenuIdList = menuService.findMenuIdList(menu.getChildId());
            //判断当前子菜单是否在父菜单下
            menuIdList.stream().filter(childMenuIdList::contains).findFirst().ifPresent(o -> {
                SysMenu sysMenu = menuService.findById(o, menu.getLanguage());
                throw new ParamValidateException("当前父菜单下存在菜单:[" + sysMenu.getMenuName() + "]，不允许新增!");
            });
        }
    }

    /**
     * 删除
     *
     * @param menuIds 菜单ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String menuIds) {
        if (ObjectUtils.isEmpty(menuIds)) {
            throw new ParamValidateException("menuIds不能为空!");
        }
        List<Long> ids = Arrays.stream(menuIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = menuService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 角色信息
     */
    @PostMapping("findById")
    public SysMenu findById(Long menuId, String language) {
        if (ObjectUtils.isEmpty(menuId)) {
            throw new ParamValidateException("menuId不能为空!");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        return menuService.findById(menuId, language);
    }

    /**
     * 根据菜单编码查询菜单信息
     *
     * @param codeList 菜单编码集合
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCodes")
    public List<SysMenu> findByCodes(@RequestBody List<String> codeList, String language) {
        return menuService.findByCodes(codeList, language);
    }

    /**
     * 查询根菜单列表
     *
     * @param projectId 项目ID
     * @param language  语言
     * @return 结果
     */
    @PostMapping("findRootMenu")
    public List<SysMenu> findRootMenu(Long projectId, String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        return menuService.findRootMenu(projectId, language);
    }

    /**
     * 根据菜单ID查询树结构
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findTreeByMenuId")
    public SysMenu findTreeByMenuId(long menuId, String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        return menuService.findTreeByMenuId(menuId, language);
    }

    /**
     * 查询当前用户的菜单
     *
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findTreeByCurrentUser")
    public SysMenu findTreeByCurrentUser(String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        if (ObjectUtils.isEmpty(currentUser.getCurrentRoleId())) {
            throw new BizException("当前用户无角色!");
        }
        //查询当前角色拥有的菜单(树结构)
        SysMenu sysMenu = menuService.findTreeByRoleId(currentUser.getCurrentRoleId(), language);
        CompletableFuture.runAsync(() -> {
            List<SysApi> apiList = findApi(Collections.singletonList(sysMenu));
            if (CollectionUtils.isEmpty(apiList)) {
                return;
            }
            //根据method分组
            Map<String, List<SysApi>> listMap = apiList.stream().collect(Collectors.groupingBy(SysApi::getApiMethod));
            Map<String, List<String>> resultMap = new HashMap<>();
            //获取API的uri
            for (Map.Entry<String, List<SysApi>> entry : listMap.entrySet()) {
                List<SysApi> sysApiList = entry.getValue();
                Set<String> apiSet = sysApiList.stream().map(SysApi::getApiUri).collect(Collectors.toSet());
                resultMap.put(entry.getKey().toLowerCase(), new ArrayList<>(apiSet));
            }
            String key = "api_auth:" + currentUser.getCurrentRoleId();
            //保存用户API权限到缓存，后续校验API权限的时候会使用
            redisUtils.set(key, resultMap);
        });
        return sysMenu;
    }

    /**
     * 首页菜单流式响应
     *
     * @param pageSize 每页条数
     * @return SSE
     */
    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Integer pageSize) throws JsonProcessingException {
        SysMenu tree = findTreeByCurrentUser("zh-CN");
        //对象转json并将数字转成字符串
        String content = LongToStrUtil.INSTANCE.cover(tree).toString();
        String regexp = "(,\"createBy\":([0-9]{1}|\"[0-9]{18}\"|null))|(\"createBy\":([0-9]{1}|\"[0-9]{18}\"|null),)";
        regexp += "|(,\"lastUpdateBy\":([0-9]{1}|\"[0-9]{18}\"|null))|(\"lastUpdateBy\":([0-9]{1}|\"[0-9]{18}\"|null),)";
        regexp += "|(,\"lastUpdateLogin\":([0-9]{1}|\"[0-9]{18}\"|null))|(\"lastUpdateLogin\":([0-9]{1}|\"[0-9]{18}\"|null),)";
        regexp += "|(,\"createDate\":([0-9]{13}|null))|(\"createDate\":([0-9]{13}|null),)";
        regexp += "|(,\"lastUpdateDate\":([0-9]{13}|null))|(\"lastUpdateDate\":([0-9]{13}|null),)";
        //剔除无用字段,减少数据包大小
        content = content.replaceAll(regexp, "");
        final String newContent = content;
        // 用于创建一个 SSE 连接对象
        SseEmitter sse = new SseEmitter();
        CompletableFuture.runAsync(() -> {
            int length = newContent.length();
            // 单块大小
            int blockSize = (pageSize == null ? 500000 : pageSize);
            // 计算分块数
            int blockCount = length / blockSize;
            // 剩余的字节数
            int remain = length % blockSize;
            try {
                if (blockCount > 0) {
                    // 循环发送分块
                    for (int i = 0; i < blockCount; i++) {
                        // 获取分块内容
                        String block = newContent.substring(i * blockSize, (i + 1) * blockSize);
                        // 发送分块内容
                        sse.send(SseEmitter.event().data(block));
                    }
                }
                if (remain > 0) {
                    // 获取剩余内容
                    String block = newContent.substring(blockCount * blockSize, length);
                    // 发送剩余内容
                    sse.send(SseEmitter.event().data(block));
                }
                //发送结束事件
                sse.send("end");
                // 完成发送
                sse.complete();
            } catch (Exception e) {
                // 发送错误
                sse.completeWithError(e);
            }
        });
        return sse;
    }

    /**
     * 根据菜单集合获取API集合
     *
     * @param menuList 菜单
     * @return API
     */
    private List<SysApi> findApi(List<SysMenu> menuList) {
        List<SysApi> apiList = new ArrayList<>();
        for (SysMenu sysMenu : menuList) {
            SysFunction function = sysMenu.getFunction();
            if (!ObjectUtils.isEmpty(function)) {
                if (!CollectionUtils.isEmpty(function.getApiList())) {
                    apiList.addAll(function.getApiList());
                }
                //获取子功能/按钮
                List<SysFunction> funChildren = function.getChildren();
                if (!CollectionUtils.isEmpty(funChildren)) {
                    //获取API权限
                    List<SysApi> sysApis = funChildren.stream().map(SysFunction::getApiList).filter(list -> !CollectionUtils.isEmpty(list)).flatMap(Collection::stream).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(sysApis)) {
                        apiList.addAll(sysApis);
                    }
                }
            }
            List<SysMenu> menuChildren = sysMenu.getChildren();
            if (!CollectionUtils.isEmpty(menuChildren)) {
                apiList.addAll(findApi(menuChildren));
            }
        }
        return apiList;
    }


    @GetMapping("findTreeByProjectCodeAndCurrentUser")
    public SysMenu findTreeByProjectCodeAndCurrentUser(@RequestParam(required = false) String language, @RequestParam(required = false) Long roleId,
                                                  @RequestParam String projectCode) {
        if (ObjectUtils.isEmpty(language)) {
            language = "zh-CN";
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误!");
        }
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();

        //查询当前角色拥有的菜单(树结构)
        SysMenu sysMenu = menuService.findTreeByProjectCodeAndCurrentUser(projectCode,currentUser.getUserId(),roleId,language);
        CompletableFuture.runAsync(() -> {
            List<SysApi> apiList = findApi(Collections.singletonList(sysMenu));
            if (CollectionUtils.isEmpty(apiList)) {
                return;
            }
            //根据method分组
            Map<String, List<SysApi>> listMap = apiList.stream().collect(Collectors.groupingBy(SysApi::getApiMethod));
            Map<String, List<String>> resultMap = new HashMap<>();
            //获取API的uri
            for (Map.Entry<String, List<SysApi>> entry : listMap.entrySet()) {
                List<SysApi> sysApiList = entry.getValue();
                Set<String> apiSet = sysApiList.stream().map(SysApi::getApiUri).collect(Collectors.toSet());
                resultMap.put(entry.getKey().toLowerCase(), new ArrayList<>(apiSet));
            }
            String key = "api_auth:" + currentUser.getCurrentRoleId();
            //保存用户API权限到缓存，后续校验API权限的时候会使用
            redisUtils.set(key, resultMap);
        });
        return sysMenu;
    }
}
