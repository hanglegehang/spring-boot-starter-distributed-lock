package cn.hang.distributed.lock;

import cn.hang.distributed.lock.redis.RedisConfig;
import cn.hang.distributed.lock.redis.RedisLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author lihang15
 * @description
 * @create 2019-01-03 13:52
 **/
@Configuration
@ConditionalOnClass(DistributedLock.class)
@EnableConfigurationProperties(DistributedLockProperty.class)
@Import({RedisConfig.class, DistributedLockAspect.class})
public class DistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    public DistributedLock redisLock() {
        return new RedisLock();
    }

}
