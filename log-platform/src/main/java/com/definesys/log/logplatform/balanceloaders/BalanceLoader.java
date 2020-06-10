package com.definesys.log.logplatform.balanceloaders;

import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/6 17:26
 * @description： 负载均衡接口，主要是在创建分区的时候使用，
 *                  可以定制不同的负载均衡方案来满足不同的需求
 * @modified By：
 * @version: 1.0
 */
public interface BalanceLoader {

    String getModuleName(List<String> names);


}
