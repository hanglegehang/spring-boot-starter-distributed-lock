package cn.hang.distributed.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author lihang15
 */
public interface DistributedLock {

    /**
     * 获取锁
     *
     * @param lockKey key
     * @return
     */
    default Boolean lock(String lockKey) {
        return false;
    }


    /**
     * 获取锁带唯一标识
     *
     * @param lockKey  key
     * @param uniqueId 唯一标识
     * @return
     */
    default Boolean lockWithUniqueId(String lockKey, String uniqueId) {
        return false;
    }

    /**
     * 获取锁，设置锁超时时长
     *
     * @param lockKey   key
     * @param leaseTime 锁超时失效时间（毫秒）
     *                  * @return
     */
    default Boolean lock(String lockKey, long leaseTime) {
        return false;
    }

    /**
     * 获取锁,设置锁超时时长,带唯一标识
     *
     * @param lockKey   key
     * @param uniqueId  唯一标识
     * @param leaseTime 锁超时失效时间（毫秒）
     * @return
     */
    default Boolean lockWithUniqueId(String lockKey, String uniqueId, long leaseTime) {
        return false;
    }


    /**
     * 获取锁，设置锁超时时长
     *
     * @param lockKey   key
     * @param leaseTime 锁超时失效时间
     * @param timeUnit  时间单位
     * @return
     */
    default Boolean lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        return false;
    }

    /**
     * 获取锁，设置锁超时时长（带唯一标示）
     *
     * @param lockKey   key
     * @param uniqueId  唯一标识
     * @param leaseTime 锁超时失效时间
     * @param timeUnit  时间单位
     * @return
     */
    default Boolean lockWithUniqueId(String lockKey, String uniqueId, long leaseTime, TimeUnit timeUnit) {
        return false;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey   key
     * @param waitTime  等待时间
     * @param leaseTime 锁超时失效时间
     * @param timeUnit  时间单位
     * @return
     */
    default Boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        return false;
    }


    /**
     * 尝试获取锁
     *
     * @param lockKey   key
     * @param waitTime  等待时间
     * @param leaseTime 锁超时失效时间（毫秒）
     * @return
     */
    default Boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        return false;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey  key
     * @param waitTime 等待时间
     * @return
     */
    default Boolean tryLock(String lockKey, long waitTime) {
        return false;
    }

    /**
     * 尝试获取锁（唯一标识）
     *
     * @param lockKey   key
     * @param uniqueId  唯一标识
     * @param waitTime  等待时间
     * @param leaseTime 锁超时失效时间
     * @param timeUnit  时间单位
     * @return
     */
    default Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime, long leaseTime, TimeUnit timeUnit) {
        return false;
    }


    /**
     * 尝试获取锁（唯一标识）
     *
     * @param lockKey   key
     * @param uniqueId  唯一标识
     * @param waitTime  等待时间
     * @param leaseTime 锁超时失效时间（毫秒）
     * @return
     */
    default Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime, long leaseTime) {
        return false;
    }

    /**
     * 尝试获取锁（唯一标识）
     *
     * @param lockKey  key
     * @param uniqueId 唯一标识
     * @param waitTime 等待时间
     * @return
     */
    default Boolean tryLockWithUniqueId(String lockKey, String uniqueId, long waitTime) {
        return false;
    }


    /**
     * 释放锁
     *
     * @param lockKey
     * @return
     */
    default Boolean unLock(String lockKey) {
        return false;
    }


    /**
     * 释放锁（带唯一标示）
     *
     * @param lockKey  key
     * @param uniqueId 唯一标识
     * @return
     */
    default Boolean unLockWithUniqueId(String lockKey, String uniqueId) {
        return false;

    }
}
