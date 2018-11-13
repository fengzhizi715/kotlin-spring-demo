package com.kotlin.tutorial.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.redis.core.RedisOperations
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.redis.RedisProperties


/**
 * Created by tony on 2018/11/13.
 */

@EnableCaching
@Configuration
@ConditionalOnClass(RedisOperations::class)
@EnableConfigurationProperties(RedisProperties::class)
open class RedisConfig : CachingConfigurerSupport() {

    @Bean(name = arrayOf("redisTemplate"))
    @ConditionalOnMissingBean(name = arrayOf("redisTemplate"))
    open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {

        val template = RedisTemplate<Any, Any>()

        val fastJsonRedisSerializer = FastJsonRedisSerializer(Any::class.java)

        template.valueSerializer = fastJsonRedisSerializer
        template.hashValueSerializer = fastJsonRedisSerializer

        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        template.connectionFactory = redisConnectionFactory
        return template
    }

    //缓存管理器
    @Bean
    open fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val builder = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
        return builder.build()
    }

}