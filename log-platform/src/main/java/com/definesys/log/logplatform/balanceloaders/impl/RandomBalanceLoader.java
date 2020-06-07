package com.definesys.log.logplatform.balanceloaders.impl;

import com.definesys.log.logplatform.balanceloaders.BalanceLoader;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/6 17:29
 * @description： 随机获取
 * @modified By：
 * @version: 1.0
 */
@Component
//加上注解自动注入会优先选择这个实现类
@Primary
public class RandomBalanceLoader implements BalanceLoader {


    @Override
    public String getModuleName(List<String> names) {
        if(!CollectionUtils.isEmpty(names)) {
            //创建节点
            int size = names.size();
            int index = (int) (Math.random() * size);
            return names.get(index);
        }
        return null;
    }
}
