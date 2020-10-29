package com.jenkin.log.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jenkin
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
 
    private static ApplicationContext applicationContext;
 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         ApplicationContextProvider.applicationContext = applicationContext;
    }
 
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
 
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
    public static List<Object> getBeanByAnno(Class anno){
        List<Object> beans = new ArrayList<>();
        String[] beanNamesForAnnotation = getApplicationContext().getBeanNamesForAnnotation(anno);
        Arrays.stream(beanNamesForAnnotation).forEach(item->{
            Object bean = getBean(item);
            beans.add(bean);
        });
        return beans;
    }
 
    public static <T> T getBean(Class<T> tClass) {
        return getApplicationContext().getBean(tClass);
    }
 
}