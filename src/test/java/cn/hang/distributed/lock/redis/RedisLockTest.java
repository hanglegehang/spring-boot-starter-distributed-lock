package cn.hang.distributed.lock.redis;


import static io.lettuce.core.dynamic.annotation.CommandNaming.Strategy.SPLIT;

public class RedisLockTest {

    public static void main(String[] args) {
        String key = "123_";
        if ("_".equals(key.substring(key.length() - 1, key.length()))) {
            key = key.substring(0, key.length() - 1);
        }
        System.out.println(key);
    }
}
