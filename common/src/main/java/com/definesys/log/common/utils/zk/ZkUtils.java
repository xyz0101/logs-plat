package com.definesys.log.common.utils.zk;

import com.alibaba.fastjson.JSON;
import com.definesys.log.common.utils.HttpUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/28 14:17
 * @description： zookeeper操作相关的工具类
 * @modified By：
 * @version: 1.0
 */
public class ZkUtils {
    private static  Logger logger =  LoggerFactory.getLogger(ZkUtils.class);
    //消息处理节点的存储路径
    private static final String PROCESSERS = "/logs-plat/process/nodes";
//    闭锁，阻塞事件监听线程
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    private static final CountDownLatch UPDATE_COUNT_DOWN_LATCH = new CountDownLatch(1);
    public static final CuratorFramework client;
    // 哈希函数接口
    private static final String URL = "http://172.16.134.98/hash/hash/getHashValue?key=";
    //服务模块节点名称存储路径，用作服务的上下线
    public static final String MODULE_NAME_PATH="/logplatform/module/names";
    //模块根路径 存储系统的主题，分区等等信息
    public static final String MODULE_NAME_ROOT="/logsync/";

    static{
        /**
         * 重试策略
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        logger.info("connect to zookeeper");
        client = CuratorFrameworkFactory.newClient("172.16.161.51:2181",
                5000, 5000, retryPolicy);
    }
    /**
     * 注册监听
     * TreeCache: 可以将指定的路径节点作为根节点（祖先节点），对其所有的子节点操作进行监听，
     * 呈现树形目录的监听，可以设置监听深度，最大监听深度为 int 类型的最大值。
     */
    public static void zkWatch(String path,ZkEventAdd add,ZkEventDelete delete)  {

        PathChildrenCache treeCache = new PathChildrenCache(client, path,true);
        //为当前节点添加一个监听，该监听会监听节点下面所有的子节点的变化
        treeCache.getListenable().addListener((client, event) -> {
            ChildData eventData = event.getData();
            if(event.getType().name().equals(PathChildrenCacheEvent.Type.CHILD_ADDED.name())){
                add.onAdded(eventData.getPath());
            }else if(event.getType().name().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED.name())){
                delete.onDelete(eventData.getPath());
            }
        });
        try {
            treeCache.start();
            COUNT_DOWN_LATCH.await();  //如果不执行 watch.countDown()，进程会一致阻塞在 watch.await()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册监听
     * TreeCache: 可以将指定的路径节点作为根节点（祖先节点），对其所有的子节点操作进行监听，
     * 呈现树形目录的监听，可以设置监听深度，最大监听深度为 int 类型的最大值。
     */
    public static void zkWatchUpdate(String path,ZkEventUpdate update)  {
        PathChildrenCache treeCache = new PathChildrenCache(client, path,true);
        //注册一个节点更新的监听，当节点更新的时候折柳就会收到通知
        treeCache.getListenable().addListener((client, event) -> {
            ChildData eventData = event.getData();
            if(event.getType().name().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED.name())){
                update.onUpdate(eventData.getPath(), new String(eventData.getData()));
            }
        });
        try {
            treeCache.start();
            UPDATE_COUNT_DOWN_LATCH.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个节点
     * @param path
     * @param value
     * @return
     */
    public static boolean createNode(String path,String value)  {
            try {
                client.create().creatingParentsIfNeeded().forPath(path, value.getBytes());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        return true;

    }


    /**
     * 注册日志处理节点,会把系统所有的processer保存在当前的节点上面
     * @param nodes
     */
    public static void registerProcessNode(Map<String, String> nodes) {
        String jsonString = JSON.toJSONString(nodes);
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .forPath(PROCESSERS,jsonString.getBytes());
        } catch (Exception e) {
            setNodeData(PROCESSERS,jsonString.getBytes());
        }
    }


    /**
     * 获取当前系统里面所有的processer节点
     * @return
     */
    public static Map<String,String> getProcessNode(){
        try {
            byte[] bytes = client.getData().forPath(PROCESSERS);
            String res = new String(bytes);
            return JSON.parseObject(res, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 修改节点的值
     * @param path
     * @param msg
     */
    public static  void setNodeData(String path,Object msg){
        try {
            client.setData().forPath(path,JSON.toJSONString(msg).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取节点数据
     * @param path
     * @return
     */
    public static <T> T getNodeData(String path,Class<T> tClass){
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String val = new String(bytes);
        return JSON.parseObject(val,tClass);
    }
    /**
     * 获取节点数据
     * @param path
     * @return
     */
    public static String getNodeData(String path){
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().forPath(path);
        } catch (Exception e) {
            logger.error(path);
            e.printStackTrace();
        }
        String s = new String(bytes);
        s=s.replaceAll("\"","");
        return s;
    }

    /**
     * 获取模块名，不要再业务系统使用
     * @return
     */
    public static String getModuleName() {
        String nodeName = getNodeName();
        System.out.println("节点名称："+nodeName);
        String moduleName = MODULE_NAME_ROOT + nodeName;

        return moduleName;
    }

    /**
     * 获取当前系统的唯一标识
     * @return
     */
    private static String getNodeName() {

        String nodeName = System.getProperty("nodeName");
        if(nodeName==null){
            throw  new RuntimeException("节点名不能为空，请在启动时加入参数： -DnodeName=XXX");
        }
        return nodeName;
    }

    /**
     * 删除一个zookeeper节点
     * @param path
     * @return
     */
    public static boolean deleteNode(String path){
        try {
            
           client.delete()
                    .forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            
        }
        return false;
    }


    /**
     * 获取已经使用了的主题和分区
     * @return
     */
    public static  List<String> getSubNodeValue(){
        String key = getModuleName();
       return getSubNodeValue(key);
    }

    /**
     * 获取本机的ip
     * @return
     */
    public static String getLocalIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return addr.getHostAddress();
    }


    /**
     * 获取kafka内部的主题下的分区
     * @param topic
     * @return
     */
    public static  List<String> getTopicPartitions(String topic){
        try {
            return client.getChildren().forPath("/brokers/topics/" + topic + "/partitions");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 获取主题下面已经使用了的分区
     * @param topic
     * @return
     */
    public static  List<String> getTopicUsedPartitions(String topic,String moduleName){
        List<String> subNodeValue = getSubNodeValue(moduleName);
        List<String> collect = subNodeValue.stream().filter(item -> item.startsWith(topic+"^")).collect(Collectors.toList());
        List<String> usedPartitions = new ArrayList<>();
        collect.forEach(item->{
            String s = item.split("\\^")[1];
            String res = resolvePartition(s, topic);
            usedPartitions.add(res);
        });
        return usedPartitions;
    }

    /**
     * 获取当前节点下面的所有子节点
     * @param path
     * @return
     */
    private static List<String> getSubNodeValue(String path) {
        try {

            List<String> childs = client.getChildren().forPath(path);
            if(childs!=null){
                return childs ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        return new ArrayList<>();
    }

    /**
     * 获取kafka里面的所有主题
     * @return
     */
    public static  List<String> getTopics(){
        try {
            return client.getChildren().forPath("/brokers/topics");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用hash函数把用户申请的分区转换为真正的kafka分区
     * @param key
     * @param topic
     * @return
     */
    public static String resolvePartition(String key,String topic){
        List<String> topicPartitions = getTopicPartitions(topic);
        int max = topicPartitions.size();
        String res = HttpUtils.getHttpRequest(URL + key + "&max=" + max);
        return res.trim();
    }

    /**
     * 启动时注册自身节点服务
     */
    public static void registerModuleName(String data) {
        if (data != null) {
            try {
                String path = MODULE_NAME_PATH + "/" + getNodeName();
                if (!isExists(path)) {
                    client.create().creatingParentsIfNeeded().forPath(path,data.getBytes());
                }else{
                    setNodeData(path,data);
                }
                logger.info("注册服务：{}=={}",getNodeName(),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new RuntimeException("注册失败，节点信息数据为空！");
        }



    }

    /**
     * 下线节点服务
     */
    public static void offlineModuleName(){
       offlineModuleName(getNodeName());
    }
     /**
     * 下线节点服务
     */
    public static void offlineModuleName(String nodeName) {
        if (!StringUtils.isEmpty(nodeName)) {
            try {
                String path = MODULE_NAME_PATH + "/" + nodeName;
                logger.warn("服务{} 下线成功",nodeName);
                if (isExists(path)) {
                    client.delete().forPath(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取可用的注册节点
     */
    public static List<String> getRegisteredModuleNames() {
        try {
            List<String> modules = new ArrayList<>();
            List<String> path = client.getChildren().forPath(MODULE_NAME_PATH);
            path.forEach(item->modules.add(MODULE_NAME_ROOT+item));
            return modules;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /**
     * 获取可用的注册节点
     * 不带根节点
     */
    public static List<String> getRegisteredModuleNamesNoPath() {
        try {
            return client.getChildren().forPath(MODULE_NAME_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     *  判断当前的topickey在系统是否存在，存在则返回他的path，否则返回null
     * @param topicKey topic^partitionKey
     * @return
     */
    public static String getExistTopicNode(String topicKey){
        for (String item : getRegisteredModuleNames()) {
            String temp = item + "/" + topicKey;
            if (ZkUtils.isExists(temp)) {
                return temp;
            }
        }
        return null;
    }
    /**
     *  判断当前的topickey在系统是否存在，存在则返回他的path，否则返回null
     * @param topicKey topic^partitionKey
     * @return
     */
    public static String getExistTopicNode(String topicKey,List<String> names){
        if (CollectionUtils.isEmpty(names)) {
            return null;
        }
        for (String item : names) {
            String temp = item + "/" + topicKey;
            if (ZkUtils.isExists(temp)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * 判断节点是否存在
     * @param path
     * @return
     */
    public static boolean isExists(String path) {
        try {
            Stat stat = ZkUtils.client.checkExists().forPath(path);
            if(stat == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
