package org.xzn.index12306.framework.starter.idempotent.core.token;

import org.xzn.index12306.framework.starter.idempotent.core.IdempotentExecuteHandler;

/**
 * @author Nruonan
 */
public interface IdempotentTokenService extends IdempotentExecuteHandler {
    /**
     * 创建幂等验证Token
     */
    String createToken();
}