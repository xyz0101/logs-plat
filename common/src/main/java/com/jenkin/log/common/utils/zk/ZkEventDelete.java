package com.jenkin.log.common.utils.zk;

@FunctionalInterface
public  interface ZkEventDelete{
    void onDelete(String data);
}