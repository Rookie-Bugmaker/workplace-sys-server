package com.crrc.sys.entity.home;

/**
 * 首页消息推送实体
 *
 * @author 谭永强
 * @date 2023-05-22
 */
public class HomeNotice {

    /**
     * 发送给目标用户ID
     */
    private Long userId;
    /**
     * 消息类型(1.工作流、2.PDA工单消息)
     */
    private Long type;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 服务名称(用于区分该消息来自哪个服务)
     */
    private String serverName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
