package cn.hang.distributed.lock.redis;

import cn.hang.distributed.lock.DistributedLock;
import cn.hang.distributed.lock.DistributedLockProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lihang15
 * @description
 * @create 2019-01-03 14:15
 **/
@Component
@Slf4j
public class RedisLock implements DistributedLock {

    private static final String LOCK_SUCCESS = "1";
    private static final String RELEASE_SUCCESS = "1";


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private DistributedLockProperty distributedLockProperty;

    @Resource(name = "getLockScript")
    private RedisScript<Object> getLockScript;

    @Resource(name = "getLockScriptUniqueIdScript")
    private RedisScript<Object> getLockScriptUniqueIdScript;

    @Resource(name = "delLockScriptUniqueIdScript")
    private RedisScript<Object> delLockScriptUniqueIdScript;

    @Override
    public Boolean lock(String lockKey) {
        return this.lock(lockKey, distributedLockProperty.getLiveTime());
    }

    @Override
    public Boolean lockWithUniqueId(String lockKey, String uniqueId) {
        return this.lockWithUniqueId(lockKey, uniqueId, distributedLockProperty.getLiveTime());
    }

    @Override
    public Boolean lock(String lockKey, long leaseTime) {
        return this.lock(lockKey, leaseTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean lockWithUniqueId(String lockKey, String uniqueId, long leaseTime) {
        return this.lockWithUniqueId(lockKey, uniqueId, leaseTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        long rawTimeout = TimeoutUtils.toMillis(leaseTime, timeUnit);
        String key = getLockKey(lockKey);
        Object eval = stringRedisTemplate.execute(getLockScript, Collections.singletonList(key), String.valueOf(rawTimeout));
        List resList = (List) eval;
        log.debug("获取分布式锁key:{},result:{}", key, eval);
        if (resList != null) {
            return LOCK_SUCCESS.equals(String.valueOf(resList.get(0)));
        }
        return false;
    }

    @Override
    public Boolean lockWithUniqueId(String lockKey, String uniqueId, long leaseTime, TimeUnit timeUnit) {
        long rawTimeout = TimeoutUtils.toMillis(leaseTime, timeUnit);
        String key = getLockKey(lockKey);
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(uniqueId);
        Object eval = stringRedisTemplate.execute(getLockScriptUniqueIdScript, keys, String.valueOf(rawTimeout));
        List resList = (List) eval;
        if (resList != null) {
            boolean result = LOCK_SUCCESS.equals(String.valueOf(resList.get(0)));
            log.debug("获取分布式锁WithUniqueId key:{},result:{}", key, result);
            return result;
        }
        return false;
    }

    @Override
    public Boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        long rawTimeout = TimeoutUtils.toMillis(leaseTime, timeUnit);
        try {
            boolean success = this.lock(lockKey, rawTimeout);
            if (!success) {
                long max = waitTime;
                long min = 50;
                long total = 0;
                if (waitTime < min) {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                } else {
                    while (max > 0) {
                        TimeUnit.MILLISECONDS.sleep(min);
                        total += min;
                        success = this.lock(lockKey, rawTimeout);
                        log.debug("休眠{}毫秒，再次尝试,结果:{}", total, success);
                        if (success) {
                            break;
                        }
                        max = max - min;
                        min = 2 * min;
                        min = Math.min(max, min);
                    }
                }
            }
            return success;
        } catch (InterruptedException e) {
            log.error("DistributedLock tryLock fail", e);
            return false;
        }
    }

    @Override
    public Boolean tryLock(String lockKey, long waitTime) {
        return this.tryLock(lockKey, waitTime, distributedLockProperty.getLiveTime());
    }

    @Override
    public Boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        return this.tryLock(lockKey, waitTime, leaseTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime, long leaseTime, TimeUnit timeUnit) {
        long rawTimeout = TimeoutUtils.toMillis(leaseTime, timeUnit);
        try {
            boolean success = this.lockWithUniqueId(lockKey, uniqueId, rawTimeout);
            if (!success) {
                long max = waitTime;
                long min = 50;
                long total = 0;
                if (waitTime < min) {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                } else {
                    while (max > 0) {
                        TimeUnit.MILLISECONDS.sleep(min);
                        total += min;
                        success = this.lockWithUniqueId(lockKey, uniqueId, rawTimeout);
                        log.debug("休眠{}毫秒，再次尝试,结果:{}", total, success);

                        log.debug("{}", success);
                        if (success) {
                            break;
                        }
                        max = max - min;
                        min = 2 * min;
                        min = Math.min(max, min);
                    }
                }
            }
            return success;
        } catch (InterruptedException e) {
            log.error("DistributedLock tryLock fail", e);
            return false;
        }
    }

    @Override
    public Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime, long leaseTime) {
        return this.tryLockWithUniqueId(lockKey, uniqueId, waitTime, leaseTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime) {
        return this.tryLockWithUniqueId(lockKey, uniqueId, waitTime, distributedLockProperty.getLiveTime());
    }

    @Override
    public Boolean unLock(String lockKey) {
        String key = getLockKey(lockKey);
        Boolean result = stringRedisTemplate.delete(key);
        log.debug("释放分布式锁key:{},result:{}", key, result);
        return result;
    }


    @Override
    public Boolean unLockWithUniqueId(String lockKey, String uniqueId) {
        String key = getLockKey(lockKey);
        Object eval = stringRedisTemplate.execute(delLockScriptUniqueIdScript, Collections.singletonList(key), String.valueOf(uniqueId));
        List resList = (List) eval;
        if (resList != null) {
            boolean result = RELEASE_SUCCESS.equals(String.valueOf(resList.get(0)));
            log.debug("释放分布式锁WithUniqueId key:{},result:{}", key, result);
            return result;
        }
        return false;
    }

    private String getLockKey(String key) {
        return distributedLockProperty.getPrefix() + key;
    }
}
