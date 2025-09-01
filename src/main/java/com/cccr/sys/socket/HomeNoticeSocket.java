package com.cccr.sys.socket;

import com.alibaba.fastjson.JSON;
import com.cccr.sys.client.AuthClient;
import com.cccr.sys.common.ResultData;
import com.cccr.sys.entity.home.HomeNotice;
import com.cccr.sys.entity.user.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 首页通知推送
 *
 * @author 谭永强
 * @date 2023-05-22
 */
@Component
@ServerEndpoint(value = "/home/notice")
public class HomeNoticeSocket {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 线程安全Map，用来存放用户ID与session的关系
     */
    private static final Map<Long, List<Session>> SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * ws注入spring对象
     */
    private static AuthClient authClient;

    @Resource
    public void setAuthClient(AuthClient authClient) {
        HomeNoticeSocket.authClient = authClient;
    }

    /**
     * 关闭连接
     *
     * @param session 会话
     */
    @OnClose
    public void onClose(Session session) {
        try {
            for (Map.Entry<Long, List<Session>> entry : SESSION_MAP.entrySet()) {
                entry.getValue().remove(session);
                break;
            }
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("连接关闭,SESSION_MAP.size()={}", SESSION_MAP.size());
    }

    /**
     * 连接异常
     *
     * @param session 会话
     * @param error   异常
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 接收消息
     *
     * @param session 会话
     * @param message 消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        if (ObjectUtils.isEmpty(message)) {
            return;
        }
        logger.info("sessionId:" + session.getId() + "发送消息:" + message);
        session.getAsyncRemote().sendText(JSON.toJSONString(ResultData.success(message)));
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session 当前连接的session
     */
    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        List<String> params = requestParameterMap.get("Authorization");
        String authorization = params.get(0);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser(authorization);
        if (!userResultData.getSuccess()) {
            session.getAsyncRemote().sendText(JSON.toJSONString(ResultData.fail(userResultData.getCode(), userResultData.getMessage())));
            this.onClose(session);
            return;
        }
        SysUser currentUser = userResultData.getResult();
        List<Session> sessionList = SESSION_MAP.get(currentUser.getUserId());
        if (CollectionUtils.isEmpty(sessionList)) {
            sessionList = new ArrayList<>();
        }
        sessionList.add(session);
        SESSION_MAP.put(currentUser.getUserId(), sessionList);
        logger.info("建立连接成功，sessionId={},SESSION_MAP.size()={}", session.getId(), SESSION_MAP.size());
        session.getAsyncRemote().sendText(JSON.toJSONString(ResultData.success("连接建立成功")));
    }

    /**
     * 消息发送(批量)
     *
     * @param noticeList 通知
     */
    public void sendMessage(List<HomeNotice> noticeList) {
        noticeList.forEach(this::sendMessage);
    }

    /**
     * 消息发送
     *
     * @param notice 通知
     */
    public void sendMessage(HomeNotice notice) {
        List<Session> sessionList = SESSION_MAP.get(notice.getUserId());
        if (!CollectionUtils.isEmpty(sessionList)) {
            for (Session session : sessionList) {
                synchronized (session) {
                    logger.debug("sessionId={},接收消息:{}", session.getId(), JSON.toJSONString(notice));
                    session.getAsyncRemote().sendText(JSON.toJSONString(ResultData.success(notice)));
                }
            }
        }
    }
}
