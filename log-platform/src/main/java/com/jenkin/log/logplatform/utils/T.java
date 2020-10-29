package com.jenkin.log.logplatform.utils;

import com.jenkin.log.common.utils.zk.ZkUtils;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 16:41
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class T {
    public static void main(String[] args) {
        ZkUtils.client.start();
        System.out.println(ZkUtils.getNodeData("/logsync/node1/testLogPlatform^1"));
    }
}
