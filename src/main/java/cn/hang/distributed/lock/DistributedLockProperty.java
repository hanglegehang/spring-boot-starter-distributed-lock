package cn.hang.distributed.lock;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lihang15
 * @description
 * @create 2019-01-03 14:35
 **/

@Component
@ConfigurationProperties(prefix = "distributed-lock")
@Data
public class DistributedLockProperty {

    private String method = "redis";

    private String defaultError = "The lock has been occupied.";

    private Long liveTime = 9000L;

    private Long timeout = 500L;

    private Integer sleepTime = 100;

    private String prefix = "DISTRIBUTED_LOCK_";

    private final DistributedLockProperty.Zookeeper zookeeper = new DistributedLockProperty.Zookeeper();

    public static class Zookeeper {

    }
}
