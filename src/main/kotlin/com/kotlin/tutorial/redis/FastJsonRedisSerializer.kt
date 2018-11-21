package com.kotlin.tutorial.redis

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException
import java.nio.charset.StandardCharsets

/**
 * Created by tony on 2018/11/13.
 */

class FastJsonRedisSerializer<T>(private val clazz: Class<T>) : RedisSerializer<T> {

    @Throws(SerializationException::class)
    override fun serialize(t: T?) = if (null == t) {
            ByteArray(0)
        } else JSON.toJSONString(t, SerializerFeature.WriteClassName).toByteArray(StandardCharsets.UTF_8)

    @Throws(SerializationException::class)
    override fun deserialize(bytes: ByteArray?): T? {

        if (bytes == null || bytes.isEmpty()) {
            return null
        }
        val str = String(bytes, StandardCharsets.UTF_8)
        return JSON.parseObject(str, clazz) as T
    }
}