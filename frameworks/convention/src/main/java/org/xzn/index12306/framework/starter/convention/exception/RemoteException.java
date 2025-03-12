package org.xzn.index12306.framework.starter.convention.exception;

import org.xzn.index12306.framework.starter.convention.errorcode.BaseErrorCode;
import org.xzn.index12306.framework.starter.convention.errorcode.IErrorCode;

/**
 * @author Nruonan
 * @description
 */
public class RemoteException extends AbstractException {

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
            "code='" + errorCode + "'," +
            "message='" + errorMessage + "'" +
            '}';
    }

}
