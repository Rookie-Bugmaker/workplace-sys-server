package com.crrc.sys.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * 向Spring容器中注入DruidConfiguration
 *
 * @author 谭永强
 * @date 2022-08-02
 */
@Configuration
public class DruidConfiguration {

    @Resource
    private MyProperties properties;

    @Bean
    @RefreshScope
    public DruidDataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        try {
            datasource.setQueryTimeout(0);
			datasource.setUrl(properties.getDbUrl());
            datasource.setUsername(properties.getUsername());
            datasource.setPassword(properties.getPassword());
            datasource.setDriverClassName(properties.getDriverClassName());
            datasource.setInitialSize(properties.getInitialSize());
            datasource.setMinIdle(properties.getMinIdle());
            datasource.setMaxActive(properties.getMaxActive());
            datasource.setMaxWait(properties.getMaxWait());
            datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
            datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
            datasource.setValidationQuery(properties.getValidationQuery());
            datasource.setTestWhileIdle(properties.isTestWhileIdle());
            datasource.setTestOnBorrow(properties.isTestOnBorrow());
            datasource.setTestOnReturn(properties.isTestOnReturn());
            datasource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
            datasource.setFilters(properties.getFilters());
            datasource.setKeepAlive(properties.isKeepAlive());
			DruidPooledConnection connection = datasource.getConnection();
			//数据库类型
			String dbType = connection.getMetaData().getDatabaseProductName();
			properties.setDbType(dbType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datasource;
    }
}