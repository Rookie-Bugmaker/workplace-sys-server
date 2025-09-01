package com.cccr.sys.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.cccr.sys.config.handler.MybatisPlusAutoFillHandler;
import com.cccr.sys.config.interceptor.EscapeLikeSqlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean的注入
 *
 * @author huangsen
 * @date 2023/03/13
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注入MybatisPlusAutoFillHandler处理器
     *
     * @return MybatisPlusAutoFillHandler处理器
     * @see MybatisPlusAutoFillHandler
     */
    @Bean
    public MybatisPlusAutoFillHandler mybatisPlusAutoFillHandler() {
        return new MybatisPlusAutoFillHandler();
    }

    /**
     * 注入mp插件
     *
     * @return MybatisPlusInterceptor mp拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 转义插件
        interceptor.addInnerInterceptor(new EscapeLikeSqlInterceptor());
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE));
        //防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        //乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
