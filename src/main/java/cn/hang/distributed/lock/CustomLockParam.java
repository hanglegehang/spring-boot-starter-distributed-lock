package cn.hang.distributed.lock;

/**
 * @author lihang15
 * @description
 * @create 2019-01-04 15:37
 **/
public interface CustomLockParam {
    /**
     * 分布式锁key
     *
     * @return
     */
    String key();
}
