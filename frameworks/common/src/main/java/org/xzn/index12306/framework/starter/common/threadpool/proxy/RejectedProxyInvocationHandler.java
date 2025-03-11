package org.xzn.index12306.framework.starter.common.threadpool.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nruonan
 * @description 线程池拒绝策略代理执行器
 */
@Slf4j
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    private final AtomicLong rejectCount;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();
        try{
            log.error("线程池执行拒绝策略，拒绝次数：{}", rejectCount.get());
            return method.invoke(target, args);
        }catch (InvocationTargetException e){
            throw e.getCause();
        }
    }
}
