package com.jenkin.log.processers;

import com.jenkin.log.entity.WarpperParam;

/**
 * 任务处理责任链
 * @author  jenkin
 */
public interface ProcessChain {


    /**
     * 节点处理模板方法
     * @param param
     
     */
    void process(WarpperParam<ProcessChain> param);

    /**
     * 节点处理之前回调钩子
     * 需要实现
     * @param param
     
     */
    void beforeProcess(WarpperParam<ProcessChain> param);

    /**
     * 节点处理之后回调钩子
     * 需要实现
     * @param param
     
     */
    void afterProcess(WarpperParam<ProcessChain> param);

    /**
     * 节点处理发生异常回调钩子
     * 需要实现
     * @param param
     
     */
    void exceptionProcess(WarpperParam<ProcessChain> param,Throwable throwable);

    /**
     * 整个责任链节点处理完成之后的回调钩子
     * 需要实现
     * @param param
     
     */
    void finishProcess(WarpperParam<ProcessChain> param);

    /**
     * 处理消息的核心逻辑，需要实现
     * @param param
     
     */
    void processMessage(WarpperParam<ProcessChain> param);

}
