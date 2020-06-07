package com.definesys.log.common.utils.zk;

@FunctionalInterface
public  interface ZkEventUpdate {
    void onUpdate(String node, String data);
}