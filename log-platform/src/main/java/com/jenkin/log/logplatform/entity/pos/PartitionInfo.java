package com.jenkin.log.logplatform.entity.pos;

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
@ApiModel("分区信息")
@Table( name = "partition_info")
@Data
public class PartitionInfo {
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "主键")
    private Integer partitionId;
    @Column(nullable = false)
    @ApiModelProperty(notes = "分区编号Key,未解析之前的分区号")
    private String partitionKey;
    @ApiModelProperty(notes = "分区编号，解析之后的分区号")
    @Column(nullable = false)
    private String partitionNumber;
    @Column()
    @ApiModelProperty(notes = "分区备注")
    private String partitionNote;
    @Column(nullable = false)
    @ApiModelProperty(notes = "分区所属的主题")
    private String partitionTopic;
    @ApiModelProperty(notes = "分区绑定的用户")
    @Column(nullable = false)
    private String partitionUser;


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
