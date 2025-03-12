package org.xzn.index12306.framework.starter.idempotent.core.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xzn.index12306.framework.starter.idempotent.core.AbstractIdempotentExecuteHandler;
import org.xzn.index12306.framework.starter.idempotent.core.IdempotentContext;
import org.xzn.index12306.framework.starter.idempotent.core.IdempotentParamWrapper;
import org.xzn.index12306.framework.starter.convention.exception.ClientException;
import org.xzn.index12306.frameworks.starter.user.core.UserContext;

/**
 * @author Nruonan
 * @description
 */
@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler  implements IdempotentParamService {

    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:param:restAPI";
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s", getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
        return IdempotentParamWrapper.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    /**
     * 获取当前线程上下文
     */
    private String getServletPath(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest().getServletPath();
    }

    /**
     * 当前用户id
     */
    private String getCurrentUserId(){
        String userId = UserContext.getUserId();
        if (StrUtil.isBlank(userId)){
            throw new ClientException("用户ID获取失败，请登录");
        }
        return userId;
    }
    /**
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String lockKey = wrapper.getLockKey();
        RLock lock = redissonClient.getLock(lockKey);
        if(!lock.tryLock()){
            throw new ClientException(wrapper.getIdempotent().message());
        }
        IdempotentContext.put(LOCK, lock);
    }

    @Override
    public void exceptionProcessing() {
        postProcessing();
    }

    @Override
    public void postProcessing() {
        RLock lock = null;
        try{
            lock = (RLock) IdempotentContext.getKey(LOCK);
        }finally {
            if (lock != null){
                lock.unlock();
            }
        }
    }
}
