package com.shiguang.rabbitmq.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.shiguang.rabbitmq.util.ConnectionUtil;

/**
 * Created By Shiguang On 2024/10/11 13:49
 */
public class Producer {
    public static final String EXCHANGE_NAME = "test_direct";
    public static void main(String[] args) throws Exception {
        // 1、获取连接
        Connection connection = ConnectionUtil.getConnection();

        // 2、获取通道
        Channel channel = connection.createChannel();

        // 3、声明交换机
        // exchange 交换机名称
        // type 交换机类型
        // durable 是否持久化
        // autoDelete 是否自动删除
        // arguments 其他参数
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false,null);

        String queue1Name = "direct_queue1";
        String queue2Name = "direct_queue2";

        // 4、创建队列
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);

        // 5、绑定队列到交换机
        // queue 队列名称
        // exchange 交换机名称
        // routingKey 路由键, 用于指定消息的路由规则
        // 队列1 绑定 error 路由键
        channel.queueBind(queue1Name, EXCHANGE_NAME, "error");
        // 队列2 绑定info、error、warning 路由键
        channel.queueBind(queue2Name, EXCHANGE_NAME, "info");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "error");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "warning");

        // 6、发送消息
        String body = "日志信息: 张三调用了delete方法. 执行出错,日志级别error";
        channel.basicPublish(EXCHANGE_NAME, "error", null, body.getBytes());

        System.out.println("body发送成功: " + body );

        // 7、释放资源
        channel.close();
        connection.close();

    }
}
