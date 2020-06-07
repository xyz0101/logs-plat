package com.definesys.log.logplatform.balanceloaders.impl;

import com.definesys.log.logplatform.balanceloaders.BalanceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/6 17:29
 * @description： 轮询获取
 * @modified By：
 * @version: 1.0
 */
@Component
public class RoundBalanceLoader implements BalanceLoader {
    //不可释放
    private static final ThreadLocal<Integer> COUNT = new ThreadLocal<>();

    @Override
    public String getModuleName(List<String> names) {
        if(!CollectionUtils.isEmpty(names)) {
            if (COUNT.get() == null) {
                COUNT.set(0);
            } else {
                if (COUNT.get() < names.size() - 1)
                    COUNT.set(COUNT.get()+1);
            }
            return names.get(COUNT.get());
        }
        return null;
    }
}
