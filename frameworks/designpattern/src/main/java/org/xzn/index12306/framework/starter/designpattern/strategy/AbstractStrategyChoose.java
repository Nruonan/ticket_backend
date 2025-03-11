package org.xzn.index12306.framework.starter.designpattern.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;
import org.xzn.index12306.framework.starter.bases.ApplicationContextHolder;
import org.xzn.index12306.framework.starter.bases.init.ApplicationInitializingEvent;
import org.xzn.index12306.frameworks.starter.convention.exception.ServiceException;


/**
 * @author Nruonan
 * @description
 */
public class AbstractStrategyChoose implements ApplicationListener<ApplicationInitializingEvent> {
    /**
     * 执行策略集合
     */
    private final Map<String, AbstractExecuteStrategy> abstractExecuteStrategyMap = new HashMap<>();

    /**
     * 根据 mark 查询具体策略
     *
     * @param mark          策略标识
     * @param predicateFlag 匹配范解析标识
     * @return 实际执行策略
     */
    public AbstractExecuteStrategy choose(String mark, Boolean predicateFlag) {
        // 检查谓词标志是否已设置且为真
        if (predicateFlag != null && predicateFlag) {
            // 如果谓词标志为真，尝试从抽象执行策略映射中查找匹配的策略
            return abstractExecuteStrategyMap.values().stream()
                    // 过滤掉模式匹配标记为空的策略
                    .filter(each -> StringUtils.hasText(each.patternMatchMark()))
                    // 进一步过滤，只保留模式匹配标记与给定标记匹配的策略
                    .filter(each -> Pattern.compile(each.patternMatchMark()).matcher(mark).matches())
                    // 找到第一个匹配的策略，如果不存在则抛出异常
                    .findFirst()
                    .orElseThrow(() -> new ServiceException("策略未定义"));
        }
        // 如果谓词标志未设置或为假，直接根据给定标记获取策略，如果不存在则抛出异常
        return Optional.ofNullable(abstractExecuteStrategyMap.get(mark))
            .orElseThrow(() -> new ServiceException(String.format("[%s] 策略未定义", mark)));
    }

    /**
     * 根据 mark 查询具体策略并执行
     *
     * @param mark         策略标识
     * @param requestParam 执行策略入参
     * @param <REQUEST>    执行策略入参范型
     */
    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark, null);
        executeStrategy.execute(requestParam);
    }

    /**
     * 根据 mark 查询具体策略并执行
     *
     * @param mark          策略标识
     * @param requestParam  执行策略入参
     * @param predicateFlag 匹配范解析标识
     * @param <REQUEST>     执行策略入参范型
     */
    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam, Boolean predicateFlag) {
        AbstractExecuteStrategy executeStrategy = choose(mark, predicateFlag);
        executeStrategy.execute(requestParam);
    }

    /**
     * 根据 mark 查询具体策略并执行，带返回结果
     *
     * @param mark         策略标识
     * @param requestParam 执行策略入参
     * @param <REQUEST>    执行策略入参范型
     * @param <RESPONSE>   执行策略出参范型
     * @return
     */
    public <REQUEST, RESPONSE> RESPONSE chooseAndExecuteResp(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark, null);
        return (RESPONSE) executeStrategy.executeResp(requestParam);
    }
    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        // 获取所有类型为 AbstractExecuteStrategy 的 Bean 实例
        Map<String, AbstractExecuteStrategy> actual = ApplicationContextHolder.getBeansOfType(AbstractExecuteStrategy.class);
        
        // 遍历每个获取到的 AbstractExecuteStrategy 实例
        actual.forEach((beanName, bean) -> {
            // 检查当前实例的标记是否已经存在于已知策略映射中
            AbstractExecuteStrategy beanExist = abstractExecuteStrategyMap.get(bean.mark());
            
            // 如果存在具有相同标记的策略，抛出异常
            if (beanExist != null) {
                throw new ServiceException(String.format("[%s] Duplicate execution policy", bean.mark()));
            }
            
            // 将当前策略实例添加到策略映射中
            abstractExecuteStrategyMap.put(bean.mark(), bean);
        });
    }
}
