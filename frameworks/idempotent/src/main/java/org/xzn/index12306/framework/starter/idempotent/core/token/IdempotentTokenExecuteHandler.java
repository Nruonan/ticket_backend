package org.xzn.index12306.framework.starter.idempotent.core.token;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xzn.index12306.framework.starter.cache.DistributedCache;
import org.xzn.index12306.framework.starter.idempotent.config.IdempotentProperties;
import org.xzn.index12306.framework.starter.idempotent.core.AbstractIdempotentExecuteHandler;
import org.xzn.index12306.framework.starter.idempotent.core.IdempotentParamWrapper;
import org.xzn.index12306.framework.starter.convention.errorcode.BaseErrorCode;
import org.xzn.index12306.framework.starter.convention.exception.ClientException;

/**
 * @author Nruonan
 * @description
 */
@RequiredArgsConstructor
public class IdempotentTokenExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentTokenService {

    private final DistributedCache distributedCache;
    private final IdempotentProperties idempotentProperties;

    private static final String TOKEN_KEY = "token";
    private static final String TOKEN_PREFIX_KEY = "idempotent:token:";
    private static final long TOKEN_EXPIRED_TIME = 6000;

    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        return  new IdempotentParamWrapper();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = request.getHeader(TOKEN_KEY);
        if (StrUtil.isBlank(token)){
            token = request.getParameter(TOKEN_KEY);
            if (StrUtil.isBlank(token)){
                throw new ClientException(BaseErrorCode.IDEMPOTENT_TOKEN_NULL_ERROR);
            }
        }
        Boolean tokenDelFlag = distributedCache.delete(token);
        if (!tokenDelFlag){
            String errMsg = StrUtil.isNotBlank(wrapper.getIdempotent().message())
                ? wrapper.getIdempotent().message()
                : BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR.message();
            throw new ClientException(errMsg, BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR);
        }
    }

    @Override
    public String createToken() {
        String token = Optional.ofNullable(Strings.emptyToNull(idempotentProperties.getPrefix())).orElse(TOKEN_PREFIX_KEY) + UUID.randomUUID();
        distributedCache.put(token, "", Optional.ofNullable(idempotentProperties.getTimeout()).orElse(TOKEN_EXPIRED_TIME));
        return token;
    }


}
