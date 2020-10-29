package com.jenkin.log.logplatform.hander;

import com.jenkin.log.common.utils.zk.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:38
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
public class StopAddDataListener implements ApplicationListener<ContextClosedEvent> {
    private Logger logger= LoggerFactory.getLogger(StopAddDataListener.class);
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if(contextClosedEvent.getApplicationContext().getParent() == null) {
            logger.info("停止spring容器");
            ZkUtils.client.close();
        }
    }
}