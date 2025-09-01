package com.cccr.sys.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 向Spring容器中注入Properties
 */
@Component
@RefreshScope
public class MyProperties {

    /**
     * 驱动
     */
    @Value("${druid.driverClassName}")
    private String driverClassName;
    /**
     * 数据库连接
     */
    @Value("${druid.url}")
    private String dbUrl;
    /**
     * 用户名
     */
    @Value("${druid.username}")
    private String username;
    /**
     * 密码
     */
    @Value("${druid.password}")
    private String password;
    /**
     * 初始化连接个数
     */
    @Value("${druid.initialSize}")
    private int initialSize;
    /**
     * 最小连接池数量
     */
    @Value("${druid.minIdle}")
    private int minIdle;
    /**
     * 最大连接池数量
     */
    @Value("${druid.maxActive}")
    private int maxActive;
    /**
     * 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
     */
    @Value("${druid.maxWait}")
    private int maxWait;
    /**
     * 1、Destroy线程会检测连接的间隔时间(默认：1分钟)；
     * 2、testWhileIdle的判断依据，详细看testWhileIdle属性的说明
     */
    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    /**
     * 连接保持空闲而不被驱逐的最小时间
     */
    @Value("${druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;
    /**
     * 检测连接是否有效，要求是一个查询语句，常用select 'x'，如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
     */
    @Value("${druid.validationQuery}")
    private String validationQuery;
    /**
     * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
     */
    @Value("${druid.testWhileIdle}")
    private boolean testWhileIdle;
    /**
     * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能，默认:true
     */
    @Value("${druid.testOnBorrow}")
    private boolean testOnBorrow;
    /**
     * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能, 默认:false
     */
    @Value("${druid.testOnReturn}")
    private boolean testOnReturn;
    /**
     * 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。默认:false
     */
    @Value("${druid.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    /**
     * 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
     * 监控统计用的filter:stat
     * 日志用的filter:log4j
     * 防御sql注入的filter:wall
     */
    @Value("${druid.filters}")
    private String filters;
    /**
     * 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作
     */
    @Value("${druid.keepAlive}")
    private boolean keepAlive;
    /**
     * 数据库类型
     */
    private String dbType;
    /**
	 * 数据库编码
     */
    private String dbEncode;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbEncode() {
        return dbEncode;
    }

    public void setDbEncode(String dbEncode) {
        this.dbEncode = dbEncode;
    }
}
