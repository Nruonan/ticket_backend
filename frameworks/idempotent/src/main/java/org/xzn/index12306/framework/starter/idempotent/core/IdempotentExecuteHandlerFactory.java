package org.xzn.index12306.framework.starter.idempotent.core;



import org.xzn.index12306.framework.starter.bases.ApplicationContextHolder;
import org.xzn.index12306.framework.starter.idempotent.annotation.Idempotent;
import org.xzn.index12306.framework.starter.idempotent.core.param.IdempotentParamService;
import org.xzn.index12306.framework.starter.idempotent.core.spel.IdempotentSpELByMQExecuteHandler;
import org.xzn.index12306.framework.starter.idempotent.core.spel.IdempotentSpELService;
import org.xzn.index12306.framework.starter.idempotent.core.token.IdempotentTokenService;
import org.xzn.index12306.framework.starter.idempotent.enums.IdempotentSceneEnum;
import org.xzn.index12306.framework.starter.idempotent.enums.IdempotentTypeEnum;

/**
 * @author Nruonan
 * @description 幂等执行处理器工厂
 */
public final class IdempotentExecuteHandlerFactory {
    /**
     * 获取幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene){
            case RESTAPI -> {
                switch (type) {
                    case TOKEN -> result = ApplicationContextHolder.getBean(IdempotentTokenService.class);
                    case PARAM -> result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                    case SPEL -> result = ApplicationContextHolder.getBean(IdempotentSpELService.class);
                    default -> {

                    }
                }
            }
            case MQ -> result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
        }
        return result;
    }
}
