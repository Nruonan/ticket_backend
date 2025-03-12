package org.xzn.index12306.framework.starter.idempotent.enums;

/**
 * @author Nruonan
 * @description 幂等验证类型美剧
 */
public enum IdempotentTypeEnum {
    // 基于TOKEN实现
    TOKEN,
    // 基于参数实现
    PARAM,
    // 基于SpEL表达式实现
    SPEL
}
