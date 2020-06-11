package com.definesys.log.logplatform.service;

import com.definesys.log.common.entity.http.PageData;
import com.definesys.log.common.utils.ApplyUtils;
import com.definesys.log.common.utils.zk.ZkUtils;
import com.definesys.log.logplatform.balanceloaders.BalanceLoader;
import com.definesys.log.logplatform.dto.LogInfoDTO;
import com.definesys.log.logplatform.dto.Productor;
import com.definesys.log.logplatform.entity.pos.PartitionInfo;
import com.definesys.log.logplatform.entity.pos.TopicInfo;
import com.definesys.log.logplatform.entity.pos.UserSystemConfig;
import com.definesys.log.logplatform.repositories.PartitionRepository;
import com.definesys.log.logplatform.repositories.TopicRepository;
import com.definesys.log.logplatform.repositories.UserSystemConfigRepository;
import com.definesys.log.logplatform.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 10:55
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Service
public class LogPlatformService {

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
        List<String> names = ZkUtils.getRegisteredModuleNames();
        int size = names.size();
        int index = (int) (Math.random()*size);
        String path = names.get(index);
        return ApplyUtils.getAvailablePartitionKey(topicKey, path);
    }

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

    public int getAvailablePartitionNumber() {
        //TODO
        return 0;
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
}
