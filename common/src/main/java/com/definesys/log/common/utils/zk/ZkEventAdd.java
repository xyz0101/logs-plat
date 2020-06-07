package com.definesys.log.common.utils.zk;

@FunctionalInterface
public interface ZkEventAdd{
    void onAdded(String data);
}
