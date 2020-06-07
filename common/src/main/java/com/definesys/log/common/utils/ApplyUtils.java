package com.definesys.log.common.utils;

import com.definesys.log.common.utils.zk.ZkUtils;

import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 14:01
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class ApplyUtils {
    public static String getAvailablePartitionKey(String topic,String moduleName){

        List<String> topicUsedPartitions = ZkUtils.getTopicUsedPartitions(topic,moduleName);
        List<String> partitions = ZkUtils.getTopicPartitions(topic);
        int max = partitions.size();
        partitions.removeAll(topicUsedPartitions);
        if(partitions.size()==0){
            throw new RuntimeException("分区已经用完！");
        }
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
