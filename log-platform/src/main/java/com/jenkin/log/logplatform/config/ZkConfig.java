package com.jenkin.log.logplatform.config;

import com.jenkin.log.common.utils.zk.ZkUtils;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/11 11:34
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Configuration
public class ZkConfig {
    private static Logger logger= LoggerFactory.getLogger(ZkConfig.class);

    static {
        if(ZkUtils.client.getState()!= CuratorFrameworkState.STARTED) {
            logger.info("start zookeeper");
            ZkUtils.client.start();
        }else{
            logger.info("zkClient已经启动");
        }
    }
}
