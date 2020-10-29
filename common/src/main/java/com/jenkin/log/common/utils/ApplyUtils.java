package com.jenkin.log.common.utils;

import com.jenkin.log.common.utils.zk.ZkUtils;

import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 14:01
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class ApplyUtils {
    /**
     * 获取一个可用的分区
     * @param topic
     * @param moduleName
     * @return
     */
    public static String getAvailablePartitionKey(String topic,String moduleName){
        //获取当前主题已经使用了的分区
        List<String> topicUsedPartitions = ZkUtils.getTopicUsedPartitions(topic,moduleName);
        //获取当前模块下面所有的分区
        List<String> partitions = ZkUtils.getTopicPartitions(topic);
        int max = partitions.size();
        //移除掉不可用分区，得到可用分区
        partitions.removeAll(topicUsedPartitions);
        if(partitions.size()==0){
            throw new RuntimeException("分区已经用完！");
        }
        //生成一个可用的分区
        for (int i = 0; i < max * 100; i++) {
            String partition = ZkUtils.resolvePartition(i + "", topic);
            if(partitions.contains(partition)){
                System.out.println("当前的key："+i+"  分区："+partition);
                return i+"";
            }
        }
        return null;
    }

}
