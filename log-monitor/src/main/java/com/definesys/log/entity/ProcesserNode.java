package com.definesys.log.entity;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 11:26
 * @description： 处理节点
 * @modified By：
 * @version: 1.0
 */

public class ProcesserNode<T> {
    public T value;
    public ProcesserNode<T> next;
}
