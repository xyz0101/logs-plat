package com.definesys.log.entity;

import com.definesys.log.common.entity.UserConfig;
import lombok.Data;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 9:48
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class WarpperParam<T> {

    private byte[] messageContent;

    private ProcesserNode<T> processerNode;

    private UserConfig userConfig;
    /**
     * 额外的消息，默认是在检查如果需要预警的时候就会把通知消息放在这里面
     * 详细见 MessageResolveProcesser
     */
    private Object extraMsg;


}
