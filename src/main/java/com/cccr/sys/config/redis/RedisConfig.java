package com.cccr.sys.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Redis缓存配置类
 *
 * @author 谭永强
 * @date 2022-08-31
 */
@Configuration
public class RedisConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.redis.type}")
    private String type;
    @Resource
    private RedisProperties redisProperties;

    public String getType() {
        return type;
    }

    public RedisProperties getRedisProperties() {
        return redisProperties;
    }

    /**
     * 初始化Redis工厂
     *
     * @return Redis工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisConnectionFactory factory;
        switch (type) {
            case "cluster":
                //集群模式
                logger.info("Redis初始化-->集群模式");
                RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
                clusterConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
                factory = new LettuceConnectionFactory(clusterConfig);
                break;
            case "sentinel":
                //哨兵模式
                logger.info("Redis初始化-->哨兵模式");
                List<String> nodes = redisProperties.getSentinel().getNodes();
                Set<RedisNode> collect = nodes.stream().map(x -> new RedisNode(x.split(":")[0], Integer.parseInt(x.split(":")[1]))).collect(Collectors.toSet());
                RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
                sentinelConfig.master(redisProperties.getSentinel().getMaster()).setSentinels(collect);
                sentinelConfig.setDatabase(redisProperties.getDatabase());
                sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
                sentinelConfig.setSentinelPassword(redisProperties.getSentinel().getPassword());
                factory = new LettuceConnectionFactory(sentinelConfig);
                break;
            case "StaticMaster":
                //主从模式
                logger.info("Redis初始化-->主从模式");
                RedisStaticMasterReplicaConfiguration masterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(redisProperties.getHost(), redisProperties.getPort());
                masterReplicaConfiguration.setDatabase(15);
                masterReplicaConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
                masterReplicaConfiguration.addNode("slave-node1", 6379);
                masterReplicaConfiguration.addNode("slave-node2", 6379);
                factory = new LettuceConnectionFactory(masterReplicaConfiguration);
                break;
            default:
                //单机模式
                logger.info("Redis初始化-->单机模式");
                RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
                standaloneConfig.setDatabase(redisProperties.getDatabase());
                standaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
                factory = new LettuceConnectionFactory(standaloneConfig);
                break;
        }
        return factory;
    }

    /**
     * 自定义一个RedisTemplate
     *
     * @return redis模版
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        //Json序列化设置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //value序列化采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value序列化采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate redis工厂
     * @return Hash操作实体
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     *
     * @param redisTemplate redis工厂
     * @return 值操作实体
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate redis工厂
     * @return List集合操作实体
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate redis工厂
     * @return Set操作实体
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate redis工厂
     * @return ZSet操作实体
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}

