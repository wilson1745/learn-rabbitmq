package com.shiguang.rabbitmq.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.shiguang.rabbitmq.util.ConnectionUtil;

/**
 * Created By Shiguang On 2024/10/11 14:36
 */
public class Producer {
    public static final String EXCHANGE_NAME = "test_topic";
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, false,null);

        String queue1Name = "topic_queue1";
        String queue2Name = "topic_queue2";

        // 4、创建队列
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);

        // 5、绑定队列到交换机
        // queue 队列名称
        // exchange 交换机名称
        // routingKey 路由键, 用于指定消息的路由规则
        // routingKey常用格式: 系统名称.日志级别
        // 需求: 所有error级别日志存入数据库,所有order系统的日志存入数据库
        channel.queueBind(queue1Name, EXCHANGE_NAME, "#.error");
        channel.queueBind(queue1Name, EXCHANGE_NAME, "order.*");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "*.*");
//        channel.queueBind(queue2Name, EXCHANGE_NAME, "#");

        // 6、发送消息
        // 分别发送消息到队列: order.info、goods.info、goods.error
        String body = "[所在系统：order][日志级别：info][日志内容: 订单生成,保存成功]";
        channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
        System.out.println("body发送成功: " + body );

        body = "[所在系统：goods][日志级别：info][日志内容: 商品发布成功]";
        channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
        System.out.println("body发送成功: " + body );

        body = "[所在系统：goods][日志级别：error][日志内容: 商品发布失败]";
        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
        System.out.println("body发送成功: " + body );

        // 7、释放资源
        channel.close();
        connection.close();

    }
}
