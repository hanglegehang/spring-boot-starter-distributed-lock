package cn.hang.distributed.lock.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author lihang15
 * @description
 * @create 2019-01-03 21:46
 **/
@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Object> getLockScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<Object>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/getLock.lua")));
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Object> getLockScriptUniqueIdScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<Object>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/getLockUniqueId.lua")));
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Object> delLockScriptUniqueIdScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<Object>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/delLock.lua")));
        redisScript.setResultType(Object.class);
        return redisScript;
    }
}
