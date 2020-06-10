package com.definesys.log.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;


/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 15:24
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
@MappedSuperclass//允许父类映射
@ApiModel("用户节点配置")
public class UserConfig {
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "主键")
    private Integer configId;
    @ApiModelProperty(notes = "用户系统的编号：主题^分区")
    @Column
    private String systemCode;

    @ApiModelProperty(notes = "日志归档的时间间隔，0 表示不归档")
    @Column
    private Integer placeOnFileTime;

    @ApiModelProperty(notes = "日志归档的时间间隔，YEAR 年，MONTH 月，DAY 日，HOUR 小时，MIN 分钟")
    @Column
    private String placeOnFileTimeUnit;

    @ApiModelProperty(notes = "日志保留的时间，0表示不存储，-1表示永久保存")
    @Column
    private Integer keepLogTime;
    @ApiModelProperty(notes = "超过了保留时间的日志的归档方式，删除还是打包，DELETE，PACKAGE")
    @Column
    private String placeOnFileType;

    @ApiModelProperty(notes = "日志保留时间的时间单位，YEAR 年，MONTH 月，DAY 日，HOUR 小时，MIN 分钟")
    @Column
    private String keepLogTimeUnit;

    @ApiModelProperty(notes = "索引后缀，比如 jenkin_mysql，最终索引会命名为 Topic_partitionKey_indexSuffix_年月日时分， 根据配置的归档单位会省略部分，例如配置到天，就会省略时，分")
    @Column
    private String indexSuffix;

    @ApiModelProperty(notes = " 通用的关键字集合，对所有processer生效，使用;分割,这个里面的关键字会对所有的通知类型进行通知")
    @Column
    private String keyWords;

    @ApiModelProperty(notes = "关键字,这里面会存储每一个处理节点对应的关键字，用JSON格式存储，比如：{'msn':{'keyWords':['error','123','错误']}} ，" +
            "这个的意思就是短信通知方式会拦截关键字有error，错误，123 的日志,这里面也可以支持其他的字段，比如通知消息模板等等")
    @Column
    private String processersKeyWords;

    @ApiModelProperty(notes = "需要忽略的节点，使用:分割")
    @Column
    private String ignoreChains;
    @ApiModelProperty(notes = "邮件发送的目标邮箱,使用:分割")
    @Column
    private String targetEmails;
    @ApiModelProperty(" 通用设置，会对所有processer生效，提醒的时候是否需要日志详细信息，当此选项为 1 的时候通知信息会在模板结束位置换行，追加上日志的详细内容")
    @Column
    private Integer needLogContent;
    @ApiModelProperty("通用设置，对所有processer生效， 通知的模板，当日志格式为JSON字符串的时候可以使用占位符 ${XXX} 替换   ，例如 存在字段" +
            "logLevel=INFO，模板为 当前日志级别是${logLevel} ,输出结果为：当前日志级别是INFO")
    private String noticeTemplate;


    @ApiModelProperty(notes = "短信通知的目标手机号，使用:分割")
    @Column
    private String msnPhoneNumber;

}
