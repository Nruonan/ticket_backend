package org.xzn.index12306.framework.starter.database.handler;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.xzn.index12306.framework.starter.distributedid.toolkit.SnowflakeIdUtil;

/**
 * @author Nruonan
 * @description
 */
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
