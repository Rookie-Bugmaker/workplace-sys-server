package com.crrc.sys.entity.server;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@TableName(value = "sys_service")
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "crrc.service")
public class ServiceAccessManagementConfig implements Serializable {
    private static final long serialVersionUID = -3145748849824571819L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 服务名
     */
    private String name;

    /**
     * 服务中文名称
     */
    private String nameCn;
    /**
     * 系统名称
     */
    private String sysName;

    /**
     * 系统描述
     */
    private String description;

    /**
     * 服务状态 0 正常 1下线
     */
    private String status;

    private Boolean sysService;

}
