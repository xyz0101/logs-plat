package com.jenkin.log.processers;

import com.jenkin.log.entity.ProcesserNode;
import com.jenkin.log.entity.WarpperParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:03
 * @description：
 * @modified By：
 * @version: 1.0
 */
public abstract class AbstractProcessChain implements ProcessChain  {
    private Logger logger = LoggerFactory.getLogger(AbstractProcessChain.class);

    /**
     * 单个节点在执行之前的操作
     * @param param
     */
    @Override
    public void beforeProcess(WarpperParam<ProcessChain> param) {
        //需要子类去实现
    }
    /**
     * 单个节点在执行之后的操作
     * @param param
     */
    @Override
    public void afterProcess(WarpperParam<ProcessChain> param) {
        //需要子类去实现

    }
    /**
     * 单个节点在执行发生异常的操作
     * @param param
     */
    @Override
    public void exceptionProcess(WarpperParam<ProcessChain> param,Throwable throwable) {
        //需要子类去实现
        logger.error("发生异常{}",throwable.getMessage());
    }
    /**
     * 所有节点在执行完成之后的操作
     * @param param
     */
    @Override
    public void finishProcess(WarpperParam<ProcessChain> param) {
        logger.info("所有的节点都已经处理完成！");
    }

    /**
     * 核心模板处理方法，在该方法里面会调用子类实现的钩子方法
     * @param param
     */
    @Override
    public void process(WarpperParam<ProcessChain> param) {
        processMsg(param);
    }

    /**
     * 处理事件
     *
     * @param param
     */
    private void processMsg(WarpperParam<ProcessChain> param) {
        beforeProcess(param);
        try {
            if (param.getProcesserNode() != null) {
                processMessage(param);
                afterProcess(param);
                ProcesserNode<ProcessChain> processerNode = param.getProcesserNode().next;
                if (processerNode != null) {
                    param.setProcesserNode(processerNode);
                    processerNode.value.process(param);
                }else{
                    finishProcess(param);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            exceptionProcess(param,e);
        }
    }
}
