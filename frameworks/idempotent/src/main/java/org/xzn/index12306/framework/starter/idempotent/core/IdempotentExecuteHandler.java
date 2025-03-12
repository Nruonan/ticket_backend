package org.xzn.index12306.framework.starter.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.xzn.index12306.framework.starter.idempotent.annotation.Idempotent;

/**
 * @author Nruonan
 * @description
 */
public interface IdempotentExecuteHandler {
    /**
     * 幂等处理逻辑
     */
    void handler(IdempotentParamWrapper idempotentParam);

    /**
     * 执行幂等处理逻辑
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent);

    /**
     * 异常流程处理
     */
    default void exceptionProcessing(){}

    /**
     * 后置处理
     */
    default void postProcessing(){}
}
