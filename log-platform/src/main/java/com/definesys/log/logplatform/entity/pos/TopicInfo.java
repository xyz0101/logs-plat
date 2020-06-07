package com.definesys.log.logplatform.entity.pos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 9:22
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@ApiModel("主题信息")
@Table( name = "topic_info")
@Data
public class TopicInfo {
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "主键")
    private Integer topicId;
    @Column(nullable = false)
    @ApiModelProperty(notes = "主题编号")
    private String topicKey;
    @Column(nullable = false)
    @ApiModelProperty(notes = "主题名称")
    private String topicName;
    @Column(nullable = false)
    @ApiModelProperty(notes = "主题备注")
    private String topicNote;

    @CreatedDate
    @Column(nullable = false)
    private Timestamp creationDate;
    @LastModifiedDate
    @Column(nullable = false)
    private Timestamp lastUpdateDate;
    @CreatedBy
    @Column
    private String createdBy;
    @LastModifiedBy
    @Column
    private String lastUpdateBy;
    @Version
    @Column
    private Integer objectVersionNumber;

}
