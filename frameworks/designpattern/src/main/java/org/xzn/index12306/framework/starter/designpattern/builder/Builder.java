package org.xzn.index12306.framework.starter.designpattern.builder;

import java.io.Serializable;

/**
 * @author Nruonan
 * @description Builder模式抽象接口
 */
public interface Builder<T> extends Serializable {

    T build();
}
