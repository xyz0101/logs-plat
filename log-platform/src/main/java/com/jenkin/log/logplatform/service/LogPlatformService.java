package com.jenkin.log.logplatform.service;

import com.google.common.collect.Lists;
import com.jenkin.log.common.entity.http.PageData;
import com.jenkin.log.common.utils.ApplyUtils;
import com.jenkin.log.common.utils.KafkaUtils;
import com.jenkin.log.common.utils.zk.ZkUtils;
import com.jenkin.log.logplatform.balanceloaders.BalanceLoader;
import com.jenkin.log.logplatform.dto.LogInfoDTO;
import com.jenkin.log.logplatform.config.Productor;
import com.jenkin.log.logplatform.entity.pos.PartitionInfo;
import com.jenkin.log.logplatform.entity.pos.TopicInfo;
import com.jenkin.log.logplatform.entity.pos.UserSystemConfig;
import com.jenkin.log.logplatform.repositories.PartitionRepository;
import com.jenkin.log.logplatform.repositories.TopicRepository;
import com.jenkin.log.logplatform.repositories.UserSystemConfigRepository;
import com.jenkin.log.logplatform.utils.CommonUtil;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 10:55
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Service
public class LogPlatformService {


    public static final String TOPIC_PREFIX = "log_plat_";

    private Logger logger = LoggerFactory.getLogger(LogPlatformService.class);
    @Resource
    private TopicRepository topicRepository;
    @Resource
    private PartitionRepository partitionRepository;
    @Resource
    private UserSystemConfigRepository userSystemConfigRepository;
    @Autowired
    private BalanceLoader balanceLoader;

    /**
     * 获取所有的主题
     * @return
     */
    public PageData<TopicInfo> listAllTopics(Pageable pageable){
        List<String> topics = ZkUtils.getTopics();
        if(topics==null){
            return new PageData<>();
        }
        topics=topics.stream().filter(item->item.startsWith(TOPIC_PREFIX)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(topics)){
            return new PageData<>();
        }
        int total = topics.size();
        int start = (pageable.getPageNumber() - 1) * pageable.getPageSize();
        int end = pageable.getPageNumber() * pageable.getPageSize();
        start=start>=total?total-1:start;
        end = end>total?total:end;
        topics = topics.subList(start, end);
        List<TopicInfo> topicInfos = topicRepository.findAllByTopicKeyIn(topics);
        final Map<String,TopicInfo> temp = new HashMap<>();
        topicInfos.forEach(item->temp.put(item.getTopicKey(),item));
        for (String topic : topics) {
            if(temp.get(topic)==null){
                TopicInfo topicInfo = new TopicInfo();
                topicInfo.setTopicKey(topic);
                topicInfos.add(topicInfo);
            }
        }
        return CommonUtil.getPageData(pageable,topicInfos,total);
    }

    /**
     * 获取我的所有节点，不区分主题
     * @param pageable
     * @return
     */
    public PageData<PartitionInfo> listAllMySystemNodes(Pageable pageable) {
        String currentUser = CommonUtil.getCurrentUser();
        Page<PartitionInfo> partitionInfos = partitionRepository.findByPartitionUser(currentUser, pageable);
        return CommonUtil.getPageData(pageable,partitionInfos.getContent(), (int) partitionInfos.getTotalElements());
    }

    public PageData<PartitionInfo> listAllMySystemNodesByTopic(String topicKey, Pageable pageable) {
        String currentUser = CommonUtil.getCurrentUser();
        Page<PartitionInfo> partitionInfos = partitionRepository.findByPartitionUserAndPartitionTopic(currentUser,topicKey, pageable);
        return CommonUtil.getPageData(pageable,partitionInfos.getContent(), (int) partitionInfos.getTotalElements());    }

    public UserSystemConfig getConfigByPartitionKey(String partitionKey) {
        return userSystemConfigRepository.getBySystemCode(partitionKey);
    }

    /**
     * 保存节点的配置
     * @param userSystemConfig
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSystemNodeConfig(UserSystemConfig userSystemConfig) {
        userSystemConfig = userSystemConfigRepository.saveAndFlush(userSystemConfig);
        String systemCode = userSystemConfig.getSystemCode();
        saveZkPartitionNodes(systemCode,userSystemConfig.getConfigId()+"",userSystemConfig.getObjectVersionNumber()+"");
    }

    private void saveZkPartitionNodes(String topicAndPartition,String configId,String versionNumber) {
        List<String> names = ZkUtils.getRegisteredModuleNames();
        String path = ZkUtils.getExistTopicNode(topicAndPartition,names);
        String val = "";
        if(configId!=null&&versionNumber!=null) {
            //修改节点
            val=configId + ";" + versionNumber;
        }
        if(path!=null){
            ZkUtils.setNodeData(path, val);

        }else{
                //创建节点
             path = balanceLoader.getModuleName(names) + "/" + topicAndPartition;
            try {
                ZkUtils.createNode(path, val);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public String applyPartitionKey(String topicKey) {
        String path = getRandomModule(topicKey);
        return ApplyUtils.getAvailablePartitionKey(topicKey, path);
    }

    /**
     * 随机获取一个模块
     * @param topicKey
     * @return
     */
    private String getRandomModule(String topicKey) {
        List<String> names = ZkUtils.getRegisteredModuleNames();
        int size = names.size();
        int index = (int) (Math.random()*size);
        String path = names.get(index);
        return path;
    }

    /**
     * 创建主题
     * @param topicInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTopic(TopicInfo topicInfo){
        List<String> topics = ZkUtils.getTopics();
        if (CollectionUtils.isEmpty(topics) ||!topics.contains(TOPIC_PREFIX + topicInfo.getTopicKey())) {
            boolean topic = createTopic(topicInfo.getTopicKey(), topicInfo.getPartitionNum(), (short) 1);
            topicInfo.setTopicKey(TOPIC_PREFIX+topicInfo.getTopicKey());
            topicInfo.setTopicName(TOPIC_PREFIX+topicInfo.getTopicName());
            if (topic) {
                topicRepository.saveAndFlush(topicInfo);
            }else{
                throw new RuntimeException("主题 ["+topicInfo.getTopicKey()+"] 创建失败！");

            }
        }else{
            throw new RuntimeException("主题 ["+topicInfo.getTopicKey()+"] 已存在！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePartitionInfo(PartitionInfo partitionInfo) {
        partitionRepository.saveAndFlush(partitionInfo);
        String key = partitionInfo.getPartitionTopic()+"^"+partitionInfo.getPartitionKey();
        saveZkPartitionNodes(key,null,null);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deletePartitionInfo(String partitionId) {
        int id = Integer.parseInt(partitionId);
        PartitionInfo partitionInfo = partitionRepository.findById(id).orElse(null);
        if (partitionInfo!=null) {
            String key = partitionInfo.getPartitionTopic() + "^" + partitionInfo.getPartitionTopic();
            partitionRepository.deleteById(id);
            String topicNode = ZkUtils.getExistTopicNode(key);
            if(topicNode !=null){
                ZkUtils.deleteNode(topicNode);
            }
        }
    }

    public int getAvailablePartitionNumber(String topic) {
        String path = getRandomModule(topic);
        //获取当前主题已经使用了的分区
        List<String> topicUsedPartitions = ZkUtils.getTopicUsedPartitions(topic,path);
        //获取当前模块下面所有的分区
        List<String> partitions = ZkUtils.getTopicPartitions(topic);
        return partitions.size()-topicUsedPartitions.size();
    }

    public void saveLogicToKafka(LogInfoDTO logInfoDTO) {
        if(!StringUtils.isEmpty(logInfoDTO.getPartitionTopic()) && !StringUtils.isEmpty(logInfoDTO.getPartitionNumber())){
            //解析真正的hash分区
            //TODO：解析真正的hash分区
            String partitionNumber = logInfoDTO.getPartitionNumber();
            this.applyPartitionKey(logInfoDTO.getPartitionTopic());
            int partition =0;
            //存储数据到队列中
            Productor productor = new Productor();
            productor.produce1(logInfoDTO.getPartitionTopic() ,partition,logInfoDTO) ;
        }else {
            try {
                throw new Exception("主题或分区编码不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private   boolean createTopic(String topicName, int partition, short replication){
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.IP_ADDR);
        AdminClient adminClient = AdminClient.create(properties);
        try {

            System.out.println(adminClient.listTopics().names().get());
            CreateTopicsResult topics = adminClient.createTopics(Lists.newArrayList(new NewTopic(TOPIC_PREFIX+topicName, partition, replication)));
            topics.all().isDone();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
//            create.close();
        }
        return false;
    }
}
