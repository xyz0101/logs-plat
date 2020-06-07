package com.definesys.log.logplatform.entity.pos;

import com.definesys.log.common.entity.UserConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 10:46
 * @description：
 * @modified By：
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Data
@Entity
@Table( name = "user_system_config")
@ApiModel("用户节点配置")
public class UserSystemConfig extends UserConfig {
    @Column
    @ApiModelProperty(notes = "用户编号")
    private String userCode;

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
