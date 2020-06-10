package com.definesys.log.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 10:30
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class PlaceholderUtils {
    private static final Logger logger = LoggerFactory.getLogger(PlaceholderUtils.class);

    /**
     * Prefix for system property placeholders: "${"
     */
    public static final String PLACEHOLDER_PREFIX = "${";
    /**
     * Suffix for system property placeholders: "}"
     */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * 占位符替换工具
     * @param text 目标文本
     * @param parameter 占位符数据源
     * @return
     */
    public static String resolvePlaceholders(String text, Map<String, Object> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return text;
        }
        if(text==null){
            return text;
        }
        StringBuffer buf = new StringBuffer(text);
        int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
                try {
                    String propVal = String.valueOf(parameter.get(placeholder));
                    if (propVal != null) {
                        buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                        nextIndex = startIndex + propVal.length();
                    } else {
                        logger.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "] ");
                    }
                } catch (Exception ex) {
                    logger.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "]: " + ex);
                }
                startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        String aa= "测试,${test}占位符， 看看${test1}行不行!";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test","我的");
        map.put("test1","占位符");
        System.out.println(PlaceholderUtils.resolvePlaceholders(aa, map));
    }

}
