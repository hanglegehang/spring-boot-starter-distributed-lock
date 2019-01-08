package cn.hang.distributed.lock;

import cn.hang.distributed.lock.annotation.DLock;
import cn.hang.distributed.lock.annotation.DLockParam;
import cn.hang.distributed.lock.annotation.DLockRequest;
import cn.hang.distributed.lock.execption.DLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

/**
 * @author lihang15
 * @description
 * @create 2019-01-03 14:15
 **/
@Aspect
@Component
@Order(-1)
public class DistributedLockAspect {

    @Autowired
    private DistributedLockProperty distributedLockProperty;

    @Autowired
    private DistributedLock distributedLock;
    private static final String SPLIT = "_";
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around(value = "@annotation(cn.hang.distributed.lock.annotation.DLock)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        // 获取注解配置的锁key值
        Method method = getMethod(jp);
        String key = getLockedKey(jp, method);
        DLock dLock = method.getAnnotation(DLock.class);

        String error = dLock.error();
        long timeout = dLock.timeout();
        boolean blocked = dLock.blocked();
        boolean withUniqueId = dLock.withUniqueId();

        if (StringUtils.isEmpty(error)) {
            error = distributedLockProperty.getDefaultError();
        }
        if (timeout == 0L) {
            timeout = distributedLockProperty.getTimeout();
        }
        boolean success = false;
        String uniqueId = UUID.randomUUID().toString();
        if (blocked) {
            if (withUniqueId) {
                success = distributedLock.tryLockWithUniqueId(key, uniqueId, timeout);
            } else {
                success = distributedLock.tryLock(key, timeout);

            }
        } else {
            if (withUniqueId) {
                success = distributedLock.lockWithUniqueId(key, uniqueId, timeout);
            } else {
                success = distributedLock.lock(key, timeout);

            }
        }

        /*
          加锁成功后执行方法并在退出时解锁
          加锁失败则判断是否抛出异常
         */
        Object result = null;
        if (success) {
            try {
                result = jp.proceed();
            } finally {
                if (withUniqueId) {
                    distributedLock.unLockWithUniqueId(key, uniqueId);
                } else {
                    distributedLock.unLock(key);
                }
            }
        } else {
            throw new DLockException(error);
        }

        return result;


    }

    private String getLockedKey(ProceedingJoinPoint jp, Method method) {
        StringBuilder key = new StringBuilder();

        // 获取 RedisLockRequest 对应的 HttpServletRequest 请求参数的值
        DLockRequest dLockRequest = method.getAnnotation(DLockRequest.class);

        if (null != dLockRequest) {
            String[] names = dLockRequest.value();

            if (names.length > 0) {
                for (String name : names) {
                    if (!StringUtils.isEmpty(name)) {
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                        Object value = request.getParameter(name);
                        if (null != value) {
                            key.append(value.toString()).append(SPLIT);
                        }
                    }
                }
            }

        }
        /*
         * 获取 RedisLockParam 对应参数的值
         */
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = jp.getArgs()[i];
            DLockParam dLockParam = parameter.getAnnotation(DLockParam.class);

            if (null != dLockParam) {
                String name = dLockParam.value();

                /*
                 * 默认返回参数本身的toString()作为锁的key值
                 * 若配置了name：
                 * 则返回参数的SpEl表达式的值
                 *
                 */
                if (StringUtils.isEmpty(name)) {
                    if (arg instanceof CustomLockParam) {
                        CustomLockParam lockable = (CustomLockParam) arg;
                        key.append(lockable.key()).append(SPLIT);
                    } else {
                        key.append(arg.toString()).append(SPLIT);
                    }
                } else {
                    key.append(parse(arg, name));
                }
            }
        }
        // 如果获取的最终key值为空，会抛出异常
        if (StringUtils.isEmpty(key.toString())) {
            throw new IllegalArgumentException("RedisLock未获取到有效的锁参数！");
        }

        // 加上前后缀
        DLock dLock = method.getAnnotation(DLock.class);
        key = new StringBuilder()
                .append(dLock.value())
                .append(SPLIT)
                .append(key.toString())
                .append(dLock.suffix());
        if (SPLIT.equals(key.substring(key.length() - 1, key.length()))) {
            return key.substring(0, key.length() - 1);
        }
        return key.toString();

    }


    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature ms = (MethodSignature) signature;
        return jp.getTarget().getClass().getMethod(ms.getName(), ms.getParameterTypes());
    }

    private String parse(Object root, String exp) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext(root);
            context.addPropertyAccessor(new MapAccessor());
            Expression expression = parser.parseExpression("#{" + exp + "}", ParserContext.TEMPLATE_EXPRESSION);
            Object cal = expression.getValue(context);
            return cal.toString();
        } catch (Exception e) {
            throw new DLockException("Unsupported lock param value: " + exp + ".");
        }
    }
}
