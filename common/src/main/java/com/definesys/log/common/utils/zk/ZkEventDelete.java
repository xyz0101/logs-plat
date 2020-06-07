package com.definesys.log.common.utils.zk;

@FunctionalInterface
public  interface ZkEventDelete{
    void onDelete(String data);
}