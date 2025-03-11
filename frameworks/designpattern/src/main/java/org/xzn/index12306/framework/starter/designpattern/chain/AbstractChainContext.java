package org.xzn.index12306.framework.starter.designpattern.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.CollectionUtils;
import org.xzn.index12306.framework.starter.bases.ApplicationContextHolder;
import org.springframework.core.Ordered;
/**
 * @author Nruonan
 * @description
 */
public class AbstractChainContext<T> implements CommandLineRunner {

    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();
    /**
     * 责任链组件执行
     *
     * @param mark         责任链组件标识
     * @param requestParam 请求参数
     */
    public void handler(String mark, T requestParam) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        abstractChainHandlers.forEach(each -> each.handler(requestParam));
    }


    @Override
    public void run(String... args) throws Exception {
        // 获取所有的责任链组件
        // 获取所有类型为AbstractChainHandler的Bean，存储在chainFilterMap中
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder.getBeansOfType(AbstractChainHandler.class);
        // 遍历chainFilterMap中的每个Bean
        chainFilterMap.forEach((beanName, bean) -> {
            // 根据Bean的标记获取对应的处理链列表
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(bean.mark());
            // 如果列表为空，则创建一个新的列表
            if (CollectionUtils.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList();
            }
            // 将当前Bean添加到列表中
            abstractChainHandlers.add(bean);
            // 对列表中的处理链进行排序，根据它们的顺序字段
            List<AbstractChainHandler> actualAbstractChainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            // 将排序后的处理链列表放回容器中
            abstractChainHandlerContainer.put(bean.mark(), actualAbstractChainHandlers);
        });
    }
}
