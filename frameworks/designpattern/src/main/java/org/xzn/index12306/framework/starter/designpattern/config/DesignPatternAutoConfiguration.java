package org.xzn.index12306.framework.starter.designpattern.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.xzn.index12306.framework.starter.bases.config.ApplicationBaseAutoConfiguration;
import org.xzn.index12306.framework.starter.designpattern.chain.AbstractChainContext;
import org.xzn.index12306.framework.starter.designpattern.strategy.AbstractStrategyChoose;

/**
 * @author Nruonan
 * @description 设计模式自动装配
 */
@ImportAutoConfiguration(ApplicationBaseAutoConfiguration.class)
public class DesignPatternAutoConfiguration {
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }

    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }
}
