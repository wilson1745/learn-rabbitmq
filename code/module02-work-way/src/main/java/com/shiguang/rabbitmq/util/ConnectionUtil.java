package com.shiguang.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created By Shiguang On 2024/10/11 12:25
 */
public class ConnectionUtil {
    public static final String HOST_ADDRESS = "192.168.10.66";
    public static final int PORT = 5672;
    public static final String VIRTUAL_HOST = "/";
    public static final String USERNAME = "guest";
    public static final String PASSWORD = "123456";

    /**
     * 获取与 RabbitMQ 服务器的连接
     *
     * @return 与 RabbitMQ 服务器的连接对象
     * @throws Exception 如果在创建连接时发生错误
     */
    public static Connection getConnection() throws Exception {
        // 创建一个新的连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 RabbitMQ 服务器的主机地址
        factory.setHost(HOST_ADDRESS);
        // 设置 RabbitMQ 服务器的端口号
        factory.setPort(PORT);
        // 设置 RabbitMQ 服务器的虚拟主机
        factory.setVirtualHost(VIRTUAL_HOST);
        // 设置连接 RabbitMQ 服务器的用户名
        factory.setUsername(USERNAME);
        // 设置连接 RabbitMQ 服务器的密码
        factory.setPassword(PASSWORD);
        // 返回新创建的连接对象
        return factory.newConnection();
    }

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("连接成功!!");
            System.out.println("connection = " + connection + "");
        } else {
            System.out.println("连接失败!!");
        }

    }


}
