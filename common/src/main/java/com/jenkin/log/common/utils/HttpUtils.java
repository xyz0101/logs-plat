package com.jenkin.log.common.utils;

import org.springframework.web.client.RestTemplate;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 13:39
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class HttpUtils {
    /**
     * 使用get的方式调用http请求
     * @param remoteUrl
     * @return
     */
    public static String getHttpRequest(String remoteUrl) {

        if (remoteUrl != null) {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(remoteUrl,String.class);
        }
        return null;
    }


}
