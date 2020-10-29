package com.jenkin.log.common.utils;

import com.jenkin.log.common.utils.zk.ZkUtils;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 15:34
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class T {
    public static void main(String[] args) {
        ZkUtils.client.start();
        ZkUtils.zkWatchUpdate("/logsync/192.168.137.1",(node, data) -> {
            System.out.println("path:  "+node);
            System.out.println("data:  "+data);
        });
    }
}
