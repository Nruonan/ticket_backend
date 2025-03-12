package org.xzn.index12306.framework.starter.convention.exception;

import org.xzn.index12306.framework.starter.convention.errorcode.BaseErrorCode;
import org.xzn.index12306.framework.starter.convention.errorcode.IErrorCode;

/**
 * @author Nruonan
 * @description
 */
public class ClientException extends AbstractException{

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
            "code='" + errorCode + "'," +
            "message='" + errorMessage + "'" +
            '}';
    }
}
