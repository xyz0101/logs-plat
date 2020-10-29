package com.jenkin.log.logplatform.controller;

import com.jenkin.log.common.entity.http.PageData;
import com.jenkin.log.common.entity.http.Response;
import com.jenkin.log.common.utils.zk.ZkUtils;
import com.jenkin.log.logplatform.dto.LogInfoDTO;
import com.jenkin.log.logplatform.entity.pos.PartitionInfo;
import com.jenkin.log.logplatform.entity.pos.TopicInfo;
import com.jenkin.log.logplatform.entity.pos.UserSystemConfig;
import com.jenkin.log.logplatform.service.LogPlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 17:01
 * @description：
 * @modified By：
 * @version: 1.0
 */
@RestController
@Api(tags = "日志平台")
@RequestMapping("/log-platform")
public class Controller {
    @Autowired
    private LogPlatformService logPlatformService;
    //获取所有的主题
    @GetMapping("/listTopics")
    @ApiOperation("获取所有的主题（日志类型），例如，MySQL，MongoDB等等")
     public Response<PageData<TopicInfo>> listTopics(String page, String pageSize){
        PageRequest pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(pageSize));
        PageData<TopicInfo> topicInfoPageData = logPlatformService.listAllTopics(pageable);
        return Response.ok(topicInfoPageData);
    }




    //获取我申请的所有节点
    @GetMapping("/listAllMySystemNodes")
    @ApiOperation("获取我申请的所有节点")
    public Response<PageData<PartitionInfo>> listAllMySystemNodes(String page,String pageSize) {
        PageRequest pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(pageSize));
        PageData<PartitionInfo> partitionInfoPageData =logPlatformService.listAllMySystemNodes(pageable);
        return Response.ok(partitionInfoPageData);
    }


    //获取我申请的主题下的所有节点
    @GetMapping("/listAllMySystemNodesByTopic")
    @ApiOperation("获取我申请的主题下的所有节点")
    public Response<PageData<PartitionInfo>> listAllMySystemNodesByTopic(String topicKey,String page,String pageSize) {
        PageRequest pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(pageSize));
        PageData<PartitionInfo> partitionInfoPageData =logPlatformService.listAllMySystemNodesByTopic(topicKey,pageable);
        return Response.ok(partitionInfoPageData);
    }
    //获取所有的责任链
    @GetMapping("/listAllProcessNodes")
    @ApiOperation("获取所有的责任链")
    public Response<List<Map>> listAllMySystemNodesByTopic() {
        List<Map> processNode = ZkUtils.getProcessNode();
        return Response.ok(processNode);
    }

    //获取当前节点的配置
    @GetMapping("/getConfigByTopicAndKey")
    @ApiOperation("获取当前节点的配置")
    public Response<UserSystemConfig> getConfigByTopicAndKey(String topic,String partitionKey){
        String key = topic+"^"+partitionKey;
        UserSystemConfig userSystemConfig = logPlatformService.getConfigByPartitionKey(key);
        return Response.ok(userSystemConfig);
    }
    //修改节点的配置
    @PostMapping("/saveSystemNodeConfig")
    @ApiOperation("保存分区节点的配置")
    public Response saveSystemNodeConfig(@RequestBody UserSystemConfig userSystemConfig ){
        logPlatformService.saveSystemNodeConfig(userSystemConfig);
        return Response.ok();
    }

    //申请主题下的分区编号
    @GetMapping("/applyPartitionKey")
    @ApiOperation("申请主题下一个可用的分区编号")
    public Response<String> applyPartitionKey(String topicKey){
        String partitionKey=logPlatformService.applyPartitionKey(topicKey);
        return Response.ok(partitionKey);
    }
    //申请创建一个系统节点
    @PostMapping("/saveTopicInfo")
    @ApiOperation("保存主题的信息")
    public Response saveTopicInfo(@RequestBody TopicInfo topicInfo){
        logPlatformService.createTopic(topicInfo);
        return Response.ok();
    }
    //申请创建一个系统节点
    @PostMapping("/savePartitionInfo")
    @ApiOperation("保存分区的信息")
    public Response savePartitionInfo(@RequestBody PartitionInfo partitionInfo){
        logPlatformService.savePartitionInfo(partitionInfo);
        return Response.ok();
    }
    //删除一个系统节点
    @GetMapping("/deletePartitionInfo")
    @ApiOperation("删除分区信息，同步任务也会中断")
    public Response deletePartitionInfo(String partitionId){
        logPlatformService.deletePartitionInfo(partitionId);
        return Response.ok();

    }

    //获取可用节点的数目
    @GetMapping("/getAvailablePartitionNumber")
    @ApiOperation("获取可用的分区数量")
    public Response<Integer> getAvailablePartitionNumber(String topic){
        int nums = logPlatformService.getAvailablePartitionNumber(topic);
        return Response.ok(nums);

    }

    //通过接口方式保存日志到kafka
    @PostMapping("/saveLogicToKafka")
    @ApiOperation("通过接口方式保存日志到kafka")
    public Response saveLogicToKafka(@RequestBody LogInfoDTO logInfoDTO){
        logPlatformService.saveLogicToKafka(logInfoDTO);
        return Response.ok();
    }
}
