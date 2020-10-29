package com.jenkin.log.common.entity;


import lombok.Data;


import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 9:22
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class TopicInfo {

    private Integer topicId;

    private String topicKey;

    private String topicName;

    private String topicNote;

    private String needResolve;

    private Timestamp creationDate;

    private Timestamp lastUpdateDate;

    private String createdBy;

    private String lastUpdateBy;

    private Integer objectVersionNumber;

}
