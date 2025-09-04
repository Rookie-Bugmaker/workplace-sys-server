package com.crrc.sys.model.datasource;

import lombok.Data;

@Data
public class DataSourceEntityDTO {
    private String id;

    private String datasourceId;

    private String datasourceName;

    private boolean mainDatasource;

    private String sysName;

    private boolean sharding;

    private String url;

    private String username;

    private String password;

    private String databaseType;

    private String driverClassName;

    private Integer deleted;
}
