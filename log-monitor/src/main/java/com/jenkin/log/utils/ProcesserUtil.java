package com.jenkin.log.utils;

import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.common.utils.ApplicationContextProvider;
import com.jenkin.log.entity.Processer;
import com.jenkin.log.entity.ProcesserNode;
import com.jenkin.log.processers.AbstractProcessChain;
import com.jenkin.log.processers.ProcessChain;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:46
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class ProcesserUtil {
    /**
     * 获取初始的processer
     * @param ignoreProcessers
     * @return
     */
    public static ProcesserNode<ProcessChain> getProcesser(String... ignoreProcessers) {
            HashSet<String> names = ignoreProcessers==null?null:new HashSet<>(Arrays.asList(ignoreProcessers));
            return getProcesser(names );
        }


        public static boolean checkHasKeywords(String keywords,String logMsg){
            if (keywords==null) {
                return  false;
            }
            for (String keyWord : keywords.split(";")) {
                if (logMsg.contains(keyWord)) {
                    return true;
                }
            }
            return false;
        }

    /**
     * 根据要忽略的处理节点获取初始的处理节点
     * 1、获取系统里面的所有processer
     * 2、用优先级队列保存节点
     * 3、过滤需要排除的processer
     * 4、吧processer封装成链表
     * 5、返回processer链表的头结点
     * @param ignoreProcessers
     * @return
     */
    public static ProcesserNode<ProcessChain> getProcesser(Set<String> ignoreProcessers) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        //获取系统里面的所有processer
        String[] beanNamesForType = applicationContext.getBeanNamesForType(AbstractProcessChain.class);
        PriorityQueue<Processer> priorityQueue = new PriorityQueue<Processer>(Comparator.comparingInt(Processer::getOrder));
        //用优先级队列保存节点
        for (String name : beanNamesForType) {
            Object wacBean = applicationContext.getBean(name);
            Class<?> aClass = wacBean.getClass();
            if(aClass.isAnnotationPresent(OrderAndName.class)) {
                OrderAndName orderAndName = aClass.getDeclaredAnnotation(OrderAndName.class);
                String key = orderAndName.name();
                int order = orderAndName.order();
                ProcessChain bean = (ProcessChain) wacBean;
                priorityQueue.add(new Processer(key,order,bean));
            }
        }
        Processer processer=priorityQueue.poll();
        ProcesserNode<ProcessChain> processerNode = new ProcesserNode<>();
        ProcesserNode<ProcessChain> currentNode = processerNode;
        if(processer!=null) {
            processerNode.value = processer.getProcesser();
            while ((processer = priorityQueue.poll()) != null) {
                //过滤需要排除的processer
                if (CollectionUtils.isEmpty(ignoreProcessers)||!ignoreProcessers.contains(processer.getName())) {
                    ProcesserNode<ProcessChain> node = new ProcesserNode<>();
//                    把processer封装成链表
                    node.value = processer.getProcesser();
                    currentNode.next = node;
                    currentNode = currentNode.next;
                }
            }
        }
//        返回processer链表的头结点
        return processerNode;
    }
}
