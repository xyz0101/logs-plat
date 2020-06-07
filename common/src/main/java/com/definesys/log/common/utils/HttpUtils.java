package com.definesys.log.common.utils;

import org.springframework.web.client.RestTemplate;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 13:39
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class HttpUtils {

    public static String getHttpRequest(String remoteUrl) {

        if (remoteUrl != null) {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(remoteUrl,String.class);
        }
        return null;
    }


}
