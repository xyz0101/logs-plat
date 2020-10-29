package com.jenkin.log.common.utils;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Title:
 * @ClassName: com.jenkin.shoppingdemo.rabbitmq.Common.java
 * @Description:
 * 交换器，队列的声明，连接的获取
 * @author: jenkin
 * @date:  2020-04-11 9:34
 * @version V1.0
 */
public class RabbitMqUtil {
    private static final Address[] ADDR = new Address[]{new Address("mall.jenkin.tech",5672),new Address("mall.jenkin.tech",5673) };
    private static final String V_HOST = "jenkin";
    private static final String USER = "jenkin";
    private static final String PASSWORD="123456";
    private static final boolean SINGLE_FLAG = false;


    /**
     * @Title:
     * @MethodName:  getConnection
     * @param
     * @Return Connection
     * @Exception
     * @Description:
     *  获取连接
     * @author: jenkin
     * @date:  2020-04-11 9:49
     */

    public static  Connection getConnection(){

        if(SINGLE_FLAG){
            return getSingleConnection();
        }
        return connect();

    }
    private static Connection connect(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            connectionFactory.setVirtualHost(V_HOST);
            connectionFactory.setUsername(USER);
            connectionFactory.setPassword(PASSWORD);
            return connectionFactory.newConnection(ADDR);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("连接为空");
    }

    public static  Connection getSingleConnection(){
        return SingleObject.getSingle();
    }

    static class SingleObject {
        private static  Connection CONNECTION= null;
        private static Connection getSingle(){
            if(CONNECTION==null) {
                CONNECTION = connect();
                System.out.println("创建连接");
            }
            return CONNECTION;
        }
    }


}
