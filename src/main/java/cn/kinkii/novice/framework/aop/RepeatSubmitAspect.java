package cn.kinkii.novice.framework.aop;

import cn.kinkii.novice.framework.service.RedisLockerService;
import cn.kinkii.novice.framework.service.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RepeatSubmitAspect {

    @Autowired
    private RedisLockerService redisLocker;

    public static final String K_HEADER_CLIENT = "K-Client";
    public static final Integer OPERATE_LOCKED = 111003;

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around("pointCut(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String clientId = request.getHeader(K_HEADER_CLIENT);
        String path = request.getServletPath();
        String redisKey = redisLocker.getRedisKeyName(clientId, path);

        boolean isSuccess = redisLocker.lock(redisKey, clientId, noRepeatSubmit.lockTime());

        if (isSuccess) {
            Object result;   // 获取锁成功
            try {
                result = pjp.proceed(); // 执行进程
            } finally {
                redisLocker.unlock(redisKey);   // 解锁
            }
            return result;
        } else {
            throw new ServiceException(OPERATE_LOCKED, "正在操作中请稍后...");
        }

    }
}
