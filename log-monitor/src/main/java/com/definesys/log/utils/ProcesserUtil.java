package com.definesys.log.utils;

import com.definesys.log.common.anno.OrderAndName;
import com.definesys.log.common.utils.ApplicationContextProvider;
import com.definesys.log.entity.Processer;
import com.definesys.log.entity.ProcesserNode;
import com.definesys.log.processers.AbstractProcessChain;
import com.definesys.log.processers.ProcessChain;
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
     * @param ignoreProcessers
     * @return
     */
    public static ProcesserNode<ProcessChain> getProcesser(Set<String> ignoreProcessers) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(AbstractProcessChain.class);
        PriorityQueue<Processer> priorityQueue = new PriorityQueue<Processer>(Comparator.comparingInt(Processer::getOrder));
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
                if (CollectionUtils.isEmpty(ignoreProcessers)||!ignoreProcessers.contains(processer.getName())) {
                    ProcesserNode<ProcessChain> node = new ProcesserNode<>();
                    node.value = processer.getProcesser();
                    currentNode.next = node;
                    currentNode = currentNode.next;
                }
            }
        }
        return processerNode;
    }
}
