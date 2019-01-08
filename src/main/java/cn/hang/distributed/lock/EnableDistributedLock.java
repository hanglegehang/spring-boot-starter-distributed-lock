package cn.hang.distributed.lock;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DistributedLockAutoConfiguration.class})
public @interface EnableDistributedLock {

}
