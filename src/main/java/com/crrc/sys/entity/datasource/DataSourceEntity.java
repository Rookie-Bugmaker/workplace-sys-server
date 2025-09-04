package com.crrc.sys.entity.datasource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_datasource")
@Data
public class DataSourceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    private String datasourceId;

    private String datasourceName;

    private String sysName;

    private boolean mainDatasource;

    private boolean sharding;

    private String url;

    private String username;

    private String password;

    private String databaseType;

    private String driverClassName;

    @TableLogic(value = "0",delval = "id")
    private Integer deleted;

}
