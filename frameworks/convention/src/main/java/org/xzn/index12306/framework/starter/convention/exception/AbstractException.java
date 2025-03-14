package org.xzn.index12306.framework.starter.convention.exception;

import java.util.Optional;
import lombok.Getter;
import org.springframework.util.StringUtils;
import org.xzn.index12306.framework.starter.convention.errorcode.IErrorCode;

/**
 * @author Nruonan
 * @description 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }

}
