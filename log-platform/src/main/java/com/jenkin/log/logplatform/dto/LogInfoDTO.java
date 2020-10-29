package com.jenkin.log.logplatform.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @Copyright: Shanghai jenkin Company.All rights reserved.
 * @Description:
 * @author: lin.zhou
 * @date: 2020/6/9 17:28
 * @history: 1.2020/6/9 created by lin.zhou
 */
@Data
public class LogInfoDTO implements Serializable {
    @ApiModelProperty(notes = "分区所属的主题")
    private String partitionTopic;

    @ApiModelProperty(notes = "分区编号")
    private String partitionNumber;

    @ApiModelProperty(notes = "IP地址")
    private String ip;

    @ApiModelProperty(notes = "环境变量")
    private String env;

    @ApiModelProperty(notes = "创建时间")
    private Timestamp creationDate;

    @ApiModelProperty(notes = "日志信息")
    private Object logger;

    @ApiModelProperty(notes = "日志的级别：DEBUG,INFO,ERROR,WARN")
    private String level;

    @ApiModelProperty(notes = "异常信息")
    private String exception;


    @ApiModelProperty(notes = "附加字段")
    private Map<String,Object> additionalFields;
}
