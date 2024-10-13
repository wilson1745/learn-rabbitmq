package com.shiguang.rabbitmq.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.shiguang.rabbitmq.util.ConnectionUtil;

/**
 * Created By Shiguang On 2024/10/11 13:08
 */
public class Producer {
    public static final String EXCHANGE_NAME = "fanout_exchange";
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, false,null);

        // 4、创建队列
        channel.queueDeclare("fanout_queue1", true, false, false, null);
        channel.queueDeclare("fanout_queue2", true, false, false, null);

        // 5、绑定队列到交换机
        // queue 队列名称
        // exchange 交换机名称
        // routingKey 路由键, 用于指定消息的路由规则
        channel.queueBind("fanout_queue1", EXCHANGE_NAME, "");
        channel.queueBind("fanout_queue2", EXCHANGE_NAME, "");

        // 6、发送消息
        String body = "日志信息: 张三调用了findAll方法 ";
        channel.basicPublish(EXCHANGE_NAME, "", null, body.getBytes());

        // 7、释放资源
        channel.close();
        connection.close();

    }
}
