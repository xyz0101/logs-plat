package com.definesys.log.logplatform.utils;

import com.definesys.log.common.entity.http.PageData;
import com.definesys.log.common.utils.zk.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 13:41
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);




    public static String getCurrentUser(){
        return "jenkin";
    }

    public static <T> PageData<T> getPageData(Pageable pageable, List<T> topics,int total) {

        PageData<T> pageData = new PageData<>();
        pageData.setCount(total);
        pageData.setPageNum(pageData.getPageNum());
        pageData.setPageSize(pageable.getPageSize());
        pageData.setResult(topics);
        return pageData;
    }
}
