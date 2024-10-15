> 在线视频:[尚硅谷2024最新RabbitMQ教程，消息中间件RabbitMQ迅速上手！](https://www.bilibili.com/video/BV1sw4m1U7Qe)
> 官方资料: [尚硅谷2024最新版RabbitMQ视频](https://pan.baidu.com/s/14quDrJSfphJfC6seNo6-CA?pwd=yyds  )
>
> 代码
> Gitee：https://gitee.com/an_shiguang/learn-rabbitmq
> GitHub: https://github.com/Shiguang-coding/learn-rabbitmq

# MQ的相关概念

## 什么是MQ

MQ(message queue),从字面意思上看，本质是个队列，FIFO先入先出，只不过队列中存放的内容是message而已，还是一种跨进程的通信机制，用于上下游传递消息。在互联网架构中，MQ是一种非常常见的上下游“逻辑解耦+物理解耦”的消息通信服务。使用了MQ之后，消息发送上游只需要依赖MQ,不用依赖其他服务。

## 为什么要用MQ

### 流量消峰

举个例子，如果订单系统最多能处理一万次订单，这个处理能力应付正常时段的下单时绰绰有余，正常时段我们下单一秒后就能返回结果。但是在高峰期，如果有两万次下单操作系统是处理不了的，只能限制订单超过一万后不允许用户下单。使用消息队列做缓冲，我们可以取消这个限制，把一秒内下的订单分散成一段时间来处理，这时有些用户可能在下单十几秒后才能收到下单成功的操作，但是比不能下单的体验要好。

![image-20241010204828111](【尚硅谷】RabbitMQ\6707cd1d08c34.png)

### 应用解耦

以电商应用为例，应用中有订单系统、库存系统、物流系统、支付系统。用户创建订单后，如果耦合调用库存系统、物流系统、支付系统，任何一个子系统出了故障，都会造成下单操作异常。当转变成基于消息队列的方式后，系统间调用的问题会减少很多，比如物流系统因为发生故障，需要几分钟来修复。在这几分钟的时间里，物流系统要处理的内存被缓存在消息队列中，用户的下单操作可以正常完成。当物流系统恢复后，继续处理订单信息即可，中单用户感受不到物流系统的故障，提升系统的可用性。

![image-20241010205039359](【尚硅谷】RabbitMQ\6707cd9f9d363.png)

### 异步处理

有些服务间调用是异步的，例如A调用B,B需要花费很长时间执行，但是A需要知道B什么时候可以执行完，以前一般有两种方式，A过一段时间去调用B的查询api查询。或者A提供一个callback api,B执行完之后调用api通知A服务。这两种方式都不是很优雅，使用消息总线，可以很方便解决这个问题，A调用B服务后，只需要监听B处理完成的消息，当B处理完成后，会发送一条消息给MQ,MQ会将此消息转发给A服务。这样A服务既不用循环调用B的查询api,也不用提供callback api。同样B服务也不用做这些操作。A服务还能及时的得到异步处理成功的消息。

![image-20241010205241250](【尚硅谷】RabbitMQ\6707ce1983cc7.png)

## MQ的分类

### 消息队列底层实现的两大主流方式

- 由于消息队列执行的是跨应用的信息传递，所以制定**底层通信标准**非常必要
- 目前主流的消息队列通信协议标准包括：
  - AMQP(Advanced Message Queuing Protocol):**通用**协议，IBM公司研发
  - JMS(Java Message Service):**专门**为**Java**语言服务，SUN公司研发，一组由Java接口组成的Java标准

### AMQP与JMS对比

![image-20241010205705458](【尚硅谷】RabbitMQ\6707cf2221479.png)

### 各主流MQ产品对比

![image-20241010210033971](【尚硅谷】RabbitMQ\6707cff25501d.png)



**1、ActiveMQ**

> [尚硅谷ActiveMQ教程(MQ消息中间件快速入门)](https://www.bilibili.com/video/BV164411G7aB)

优点：单机吞吐量万级，时效性ms级，可用性高，基于主从架构实现高可用性，消息可靠性较低的概率丢失数据
缺点：官方社区现在对ActiveMQ5.x维护越来越少，高吞吐量场景较少使用。

**2、Kafka**

> [尚硅谷Kafka教程，2024新版kafka视频，零基础入门到实战](https://www.bilibili.com/video/BV1Gp421m7UN)
>
> [【尚硅谷】Kafka3.x教程（从入门到调优，深入全面）](https://www.bilibili.com/video/BV1vr4y1677k)

大数据的杀手锏，谈到大数据领域内的消息传输，则绕不开Kafka,这款**为大数据而生**的消息中间件，以其**百万级TPS**的吞吐量名声大噪，迅速成为大数据领域的宠儿，在数据采集、传输、存诸的过程中发挥着举足轻重的作用。目前已经被LinkedIn，Uber，Twitter，Netflix等大公司所采纳。

优点：性能卓越，单机写入TPS约在百万条/秒，最大的优点，就是**吞吐量高**。时效性ms级可用性非常高，kafka是分布式的，一个数据多个副本，少数机器宕机，不会丢失数据，不会导致不可用，消费者采用Pull方式获取消息，消息有序，通过控制能够保证所有消息被消费且仅被消费一次;有优秀的第三方Kafka Web管理界面Kafka-Manager;在日志领域比较成熟，被多家公司和多个开源项目使用；功能支持：功能较为简单，主要支持简单的MQ功能，在大数据领域的实时计算以及**日志采集**被大规模使用

缺点：Kafka单机超过64个队列/分区，Load会发生明显的飙高现象，队列越多，load越高，发送消息响应时间变长，使用短轮询方式，实时性取决于轮询间隔时间，消费失败不支持重试；支持消息顺序，但是一台代理宕机后，就会产生消息乱序，**社区更新较慢**；

**3、RocketMQ**

> [【尚硅谷】RocketMQ教程丨深度掌握MQ消息中间件](https://www.bilibili.com/video/BV1cf4y157sz)

RocketMQ出自阿里巴巴的开源产品，用java语言实现，在设计时参考了Kafka,并做出了自己的一些改进。被阿里巴巴广泛应用在订单，交易，充值，流计算，消息推送，日志流式处理，binglog分发等场景。

优点：单**机吞吐量十万级**，可用性非常高，分布式架构，**消息可以做到0丢失**，MQ功能较为完善，还是分布式的，扩展性好，**支持10亿级别的消息堆积**，不会因为堆积导致性能下降，源码是jva我们可以自己阅读源码，定制自己公司的MQ

缺点：**支持的客户端语言不多**，目前是java及c++,其中c++不成熟；社区活跃度一般，没有在MQ核心中去实现JMS等接口，有些系统要迁移需要修改大量代码

**4、RabbitMQ**

2007年发布，是一个在AMQP(高级消息队列协议)基础上完成的，可复用的企业消息系统，是**当前最主流的消息中间件之一**。

优点：由于erlang语言的**高并发特性**，性能较好；**吞吐量到万级**，MQ功能比较完备，健壮、稳定、易用、跨平台、**支持多种语言**如：Python、Ruby、.NET、Java、JS、C、PHP、ActionScript、XMPP、STOMP等，支持A]AX文档齐全；开源提供的管理界面非常棒，用起来很好用，**社区活跃度高**；更新频率相当高

缺点：商业版需要收费，学习成本较高

## MQ的选择

**1、Kafka**
Kafka主要特点是基于Pul的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集和传输，适合产生**大量数据**的互联网服务的数据收集业务。**大型公司**建议可以选用，如果有**日志采集**功能，肯定是首选kafka了。

**2、RocketMQ**
天生为**金融互联网**领域而生，对于可靠性要求很高的场景，尤其是电商里面的订单扣款，以及业务削峰，在大量交易涌入时，后端可能无法及时处理的情况。RoketMQ在稳定性上可能更值得信赖，这些业务场景在阿里双11已经经历了多次考验，如果你的业务有上述并发场景，建议可以选择RocketMQ。

**3、RabbitMQ**
结合erlang语言本身的并发优势，性能好**时效性微秒级**，**社区活跃度也比较高**，管理界面用起来十分
方便，如果你的**数据量没有那么大**，中小型公司优先选择功能比较完备的RabbitMQ。

# RabbitMQ介绍

## RabbitMQ的概念

RabbitMQ是一个消息中间件：它接受并转发消息。你可以把它当做一个快递站点，当你要发送一个包裹时，你把你的包裹放到快递站，快递员最终会把你的快递送到收件人那里，按照这种逻辑RabbitMQ是一个快递站，一个快递员帮你传递快件。RabbitMQ与快递站的主要区别在于，它不处理快件而是接收，存储和转发消息数据。

## 四大核心概念

**生产者**
产生数据发送消息的程序是生产者

**交换机**
交换机是RabbitMQ非常重要的一个部件，一方面它接收来自生产者的消息，另一方面它将消息推送到队列中。交换机必须确切知道如何处理它接收到的消息，是将这些消息推送到特定队列还是推送到多个队列，亦或者是把消息丢弃，这个得有交换机类型决定。

**队列**

队列是RabbitMQ内部使用的一种数据结构，尽管消息流经RabbitMQ和应用程序，但它们只能存储在队列中。队列仅受主机的内存和滋盘限制的约束，本质上是一个大的消息缓冲区。许多生产者可以将消息发送到一个队列，许多消费者可以尝试从一个队列接收数据。这就是我们使用队列的方式。

**消费者**
消费与接收具有相似的含义。消费者大多时候是一个等待接收消息的程序。请注意生产者，消费者和消息中间件很多时候并不在同一机器上。同一个应用程序既可以是生产者又是可以是消费者。

![image-20241010213015369](【尚硅谷】RabbitMQ\6707d6e7a8ae4.png)

## RabbitMQ核心部分

![image-20241010213451006](【尚硅谷】RabbitMQ\6707d7fb6d265.png)

## 各个名词介绍

![image-20241010213523739](【尚硅谷】RabbitMQ\6707d81c1fa11.png)

**Broker**：接收和分发消息的应用， RabbitMQ Server 就是 Message Broker

**Virtual host**：出于多租户和安全因素设计的，把 AMQP 的基本组件划分到一个虚拟的分组中，类似于网络中的 namespace 概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出多个 vhost，每个用户在自己的 vhost 创建 exchange／ queue 等

**Connection**： publisher／ consumer 和 broker 之间的 TCP 连接

**Channel**：如果每一次访问 RabbitMQ 都建立一个 Connection，在消息量大的时候建立 TCP Connection 的开销将是巨大的，效率也较低。 Channel 是在 connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个 thread 创建单独的 channel 进行通讯， AMQP method 包含了 channel id 帮助客户端和 message broker 识别 channel，所以 channel 之间是完全隔离的。 Channel 作为轻量级的**Connection 极大减少了操作系统建立 TCP connection 的开销**

**Exchange**： message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发消息到 queue 中去。常用的类型有： direct (point-to-point), topic (publish-subscribe) and fanout (multicast)

**Queue**： 消息最终被送到这里等待 consumer 取走

**Binding**： exchange 和 queue 之间的虚拟连接， binding 中可以包含 routing key， Binding 信息被保存到 exchange 中的查询表中，用于 message 的分发依据

# 安装RabbitMQ

## 手动安装

**1、官网地址**

https://www.rabbitmq.com/download.html

2、**文件上传上传到`/usr/local/software` 目录下**(如果没有 software 需要自己创建)

![image-20241010214418997](【尚硅谷】RabbitMQ\6707da33347fe.png)

**3、安装文件(分别按照以下顺序安装)**  

```bash
rpm -ivh erlang-21.3-1.el7.x86_64.rpm
yum install socat -y
rpm -ivh rabbitmq-server-3.8.8-1.el7.noarch.rpm
```

**4、常用命令(按照以下顺序执行)**

添加开机启动 RabbitMQ 服务

```bash
chkconfig rabbitmq-server on
```

启动服务

```bash
/sbin/service rabbitmq-server start
```

查看服务状态

```bash
/sbin/service rabbitmq-server status
```

![image-20241010214909944](【尚硅谷】RabbitMQ\6707db57417c8.png)

停止服务(选择执行)

```bash
/sbin/service rabbitmq-server stop
```

开启 web 管理插件

```bash
rabbitmq-plugins enable rabbitmq_management
```

用默认账号密码(guest)访问地址 `http://47.115.185.244:15672`出现权限问题  

![image-20241010214756433](【尚硅谷】RabbitMQ\6707db0cbee0a.png)

**5、添加一个新的用户**

创建账号

```bash
rabbitmqctl add_user admin 123
```

设置用户角色

```bash
rabbitmqctl set_user_tags admin administrator
```

设置用户权限

```bash
# set_permissions [-p <vhostpath>] <user> <conf> <write> <read> 
rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
```

用户 user_admin 具有/vhost1 这个 virtual host 中所有资源的配置、写、读权限

当前用户和角色

```bash
rabbitmqctl list_users
```

**6、再次利用 admin 用户登录**  

![image-20241010215234896](【尚硅谷】RabbitMQ\6707dc2323d67.png)

**7、重置命令**

关闭应用的命令为:

```bash
rabbitmqctl stop_app
```

清除的命令为

```bash
rabbitmqctl reset
```

重新启动命令为

```bash
rabbitmqctl start_app
```

## Docker安装

**1、安装**

```bash
# 拉取镜像
docker pull rabbitmq:3.13-management

# -d 参数：后台运行 Docker 容器
# --name 参数：设置容器名称
# -p 参数：映射端口号,格式为 "宿主机端口号:容器内部端口号" 5672供客户端程序访问,15672供后台应用管理界面访问
# -v 参数：卷映射目录
# -e 参数：设置容器内的环境变量,这里我们设置了登录RabbitMQ管理后台的默认用户和密码
docker run -d \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
-v rabbitmq-plugin:/plugins \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=123456 \
rabbitmq:3.13-management
```

**2、验证**

访问后台管理界面： `http://<ip>:15672`

![image-20241010224411426](【尚硅谷】RabbitMQ\6707e83b952cd.png)

登录后界面如图:
![image-20241010224526884](【尚硅谷】RabbitMQ\6707e88738459.png)

# Hello World

我们将用java编写两个程序。发送单个消息的生产者和接收消息并打印出来的消费者。
下图中，”P”是我们的生产者，”C”是我们的消费者。中间的框是一个队列-RabbitMQ 代表使用者保留的消息缓冲区

![image-20241011095319454](【尚硅谷】RabbitMQ\670885105d9c1.png)

## 导入依赖

```pom
<dependency>
   <groupId>com.rabbitmq</groupId>
   <artifactId>amqp-client</artifactId>
   <version>5.20.0</version>
</dependency>
```

## 消息发送端(生产者)

```JAVA
package com.shiguang.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created By Shiguang On 2024/10/11 10:05
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 设置主机地址
        connectionFactory.setHost("192.168.10.66");

        // 设置端口号: 默认为5672
        connectionFactory.setPort(5672);

        // 虚拟主机名称: 默认为/
        connectionFactory.setVirtualHost("/");

        // 设置连接用户名: 默认为guest
        connectionFactory.setUsername("guest");

        // 设置连接密码: 默认为guest
        connectionFactory.setPassword("123456");

        // 创建连接
        Connection connection = connectionFactory.newConnection();

        // 创建通道
        Channel channel = connection.createChannel();

        // 声明队列
        // queue 名称
        // durable 是否持久化
        // exclusive 是否独占本次连接,若为true,则队列仅在本次连接可见,连接关闭后,队列自动删除
        // autoDelete 是否自动删除,若为true,则当最后一个消费者断开连接后,队列会被删除
        // arguments 其他参数
        channel.queueDeclare("simple_queue", false, false, false, null);

        // 发布消息
        String message = "hello rabbitmq";

        // exchange 交换机名称
        // routingKey 路由键,用于将消息路由到指定的队列,如果没有指定,消息将发送到默认的交换机,默认的交换机名称为空字符串
        // props 消息属性,用于设置消息的属性,如消息的优先级、过期时间等
        // body 消息体,即要发送的消息内容
        channel.basicPublish("", "simple_queue", null, message.getBytes());

        System.out.println("消息已发送:" + message + "");

        // 关闭资源
        channel.close();
        connection.close();

    }
}
```

执行后如下所示：

![image-20241011115518386](【尚硅谷】RabbitMQ\6708a1a724d9e.png)

可以在后台管理界面查看状态

![image-20241011115406548](【尚硅谷】RabbitMQ\6708a15f7c5d7.png)

查看消息队列

![image-20241011115428733](【尚硅谷】RabbitMQ\6708a1759f72f.png)

## 消息接收端(消费者)

```JAVA
package com.shiguang.rabbitmq.simple;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 11:17
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1、创建一个ConnectionFactory，并设置主机名、端口号、虚拟主机、用户名和密码。
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接参数。
        factory.setHost("192.168.10.66");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("123456");

        // 3、通过工厂创建连接。
        Connection connection = factory.newConnection();

        // 4、通过连接创建通道。
        Channel channel = connection.createChannel();

        // 5、创建队列，并指定队列名称、是否持久化、是否独占、是否自动删除、其他参数。
        // 生产者已经创建了队列，这里不需要再创建
//        channel.queueDeclare("simple.queue", true, false, false, null);

        // 6、消费消息
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            // consumerTag 消费者标签，用来标识消费者，在监听消息时使用。
            // envelope 消息的元数据，包括交换机、路由键、投递模式等。
            // properties 消息的属性，如消息的优先级、过期时间等。
            // body 消息的正文，即要发送的数据。
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("consumerTag: " + consumerTag);
                System.out.println("Exchange: " + envelope.getExchange());
                System.out.println("RoutingKey: " + envelope.getRoutingKey());
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }
        };

        // 7、注册消费者，指定队列名称、是否自动应答、消费者。
        channel.basicConsume("simple_queue", true, consumer);

        // 8、关闭资源
        channel.close();
        connection.close();

    }
}
```

执行结果如下：

![image-20241011115754203](【尚硅谷】RabbitMQ\6708a2430c500.png)

再次查看状态

![image-20241011115834136](【尚硅谷】RabbitMQ\6708a26b0a9ca.png)

再次查看消息队列

![image-20241011115900482](【尚硅谷】RabbitMQ\6708a2856c188.png)

# RabbitMQ工作模式

## 工作模式概述

RabbitMQ有7种用法：

![image-20241011120726844](【尚硅谷】RabbitMQ\6708a47fbeda7.png)

以下是 RabbitMQ 的一些常见用法：

1. 消息队列：

   RabbitMQ 最基本的用法是作为消息队列。生产者将消息发送到 RabbitMQ 服务器，消费者从队列中获取消息并进行处理。这种模式可以实现应用程序的解耦和异步通信。

2. 发布/订阅模式：

   RabbitMQ 支持发布/订阅模式，允许生产者将消息发布到一个或多个交换机（Exchange），消费者订阅感兴趣的队列。当有新消息到达时，RabbitMQ 会将消息路由到所有订阅了相应队列的消费者。

3. 路由模式：

   在路由模式中，生产者将消息发送到交换机，并指定一个路由键（Routing Key）。RabbitMQ 根据路由键将消息路由到绑定了相应路由键的队列。这种模式可以实现更精细的消息路由。

4. 主题模式：

   主题模式是路由模式的扩展，允许使用通配符来匹配路由键。例如，可以使用“*”通配符匹配一个单词，使用“#”通配符匹配任意数量的单词。这种模式可以实现更灵活的消息路由。

5. RPC（远程过程调用）：

   RabbitMQ 可以用于实现 RPC 机制，允许客户端调用远程服务器上的方法。客户端将请求消息发送到 RabbitMQ，服务器处理请求并将响应消息发送回客户端。

## Work Queues

工作队列(又称任务队列)的主要思想是避免立即执行资源密集型任务，而不得不等待它完成。相反我们安排任务在之后执行。我们把任务封装为消息并将其发送到队列。在后台运行的工作进程将弹出任务并最终执行作业。 当有多个工作线程时，这些工作线程将一起处理这些任务。 

 

本质上我们刚刚写的HelloWorld程序就是这种模式，只是简化到了最简单的情况：

- 生产者只有一个
- 发送一个消息
- 消费者也只有一个，消息也只能被这个消费者消费

所以HelloWorld也称为简单模式。



现在我们还原一下常规情况：

- 生产者发送多个消息
- 由多个消费者来竞争
- 谁抢到算谁的

结论：
多个消费者监听同一个队列，则各消费者之间对同一个消息是竞争的关系。
Work Queues工作模式适用于任务较重或任务较多的情况，多消费者分摊任务，可以提高消息处理的效率

### 生产者代码

#### 封装工具类

```java
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
```

#### 编写代码

```java
package com.shiguang.rabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.shiguang.rabbitmq.util.ConnectionUtil;

/**
 * Created By Shiguang On 2024/10/11 12:30
 */
public class Producer {
    public static final String QUEUE_NAME = "work_queue";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for (int i = 1; i <= 10; i++) {
            String body = i + "hello rabbitmq";
            channel.basicPublish("", QUEUE_NAME, null, body.getBytes());
        }

        channel.close();

    }
}
```

#### 发送消息效果

![image-20241011124518118](【尚硅谷】RabbitMQ\6708ad5f0d0d5.png)

### 消费者代码

#### 编写代码

Consumer1：

```java
package com.shiguang.rabbitmq.work;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 12:46
 */
public class Consumer1 {
    static final String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer1 Body: " + new String(body));
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
```

Consumer2：

```java
package com.shiguang.rabbitmq.work;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 12:46
 */
public class Consumer2 {
    static final String QUEUE_NAME = "work_queue";
    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer2 Body: " + new String(body));
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```

#### 运行效果

Consumer1：

![image-20241011125514415](【尚硅谷】RabbitMQ\6708afb32f01c.png)

Consumer2:

![image-20241011125554724](【尚硅谷】RabbitMQ\6708afdb74210.png)

## 发布订阅模式（Publish/Subscribe）

Publish/Subscribe模式需要引入新角色：交换机

- 生产者不是把消息直接发送到队列，而是发送到交换机
- 交换机接收消息，而如何处理消息取决于交换机的类型
- 交换机有如下3种常见类型
  - Fanout: 广播，将消息发送给所有绑定到交换机的队列
  - Direct: 定向，把消息交给符合指定routing key的队列
  - Topic: 通配符，把消息交给符合routing pattern(路由模式)的队列

注意：Exchange(交换机)**只负责转发**消息，**不具备存储**消息的能力，因此如果没有任何队列与Exchange绑定，或者没有符合路由规侧的队列，那么消息会丢失！



组件之间关系：

- 生产者把消息发送到交换机
- 队列直接和交换机绑定

工作机制：消息发送到交换机上，就会以**广播**的形式发送给所有已绑定队列

理解概念：

- Publish:发布，这里就是把消息发送到交换机上
- Subscribe:订阅，这里只要把队列和交换机绑定，事实上就形成了一种订阅关系

### 生产者代码

#### 编写代码

```java
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
```



#### 执行效果

可以通过后台查看我们刚创建的交换机

![image-20241011132250767](【尚硅谷】RabbitMQ\6708b62baa05d.png)

点击 `Name` 栏的交换机名称跳转到详情页，展开`Bindings`查看该交换机绑定的消息队列

![image-20241011132455222](【尚硅谷】RabbitMQ\6708b6a8161c8.png)

可以看到新增两个消息队列并分别发送了一条消息

![image-20241011132707238](【尚硅谷】RabbitMQ\6708b72c07708.png)

点击`Name`栏的消息队列名称可查看详情

![image-20241011132939726](【尚硅谷】RabbitMQ\6708b7c48f285.png)

通过`Get Messages(s)`按钮可以查看消息详情

![image-20241011133113587](【尚硅谷】RabbitMQ\6708b8224c1e8.png)

### 消费者代码

#### 编写代码

**Consumer1：**

```java
package com.shiguang.rabbitmq.fanout;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 13:32
 */
public class Consumer1 {
    static final String QUEUE_NAME = "fanout_queue1";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer1 Body: " + new String(body));
                System.out.println("队列1 消费者1 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
```

**Consumer2：**

```java
package com.shiguang.rabbitmq.fanout;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 13:32
 */
public class Consumer2 {
    static final String QUEUE_NAME = "fanout_queue2";
    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer2 Body: " + new String(body));
                System.out.println("队列2 消费者2 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```

#### 执行效果

> 示例代码两个Consumer分别绑定不同的消息队列，为非竞争关系，若绑定相同的消息队列则为竞争关系

Consumer1：

![image-20241011134111290](【尚硅谷】RabbitMQ\6708ba78076b5.png)

Consumer2：

![image-20241011134139332](【尚硅谷】RabbitMQ\6708ba940f5bd.png)

## 路由模式（Routing）

- 通过 **路由绑定 **的方式，把交换机和队列关联起来
- 交换机和队列通过路由键进行绑定
- 生产者发送消息时不仅要指定交换机，还要指定路由键
- 交换机接收到消息会发送到路由键绑定的队列
- 在编码上与Publish/Subscribe发布与订阅模式的区别：
  - 交换机的类型为：Direct
  - 队列绑定交换机的时候需要指定routing key。

![image-20241011134835789](【尚硅谷】RabbitMQ\6708bc347bd0f.png)



### 生产者代码

#### 编写代码

```java
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
        String body = "日志信息: 张三调用了delete方法. 执行出错,日志级别warning";
        channel.basicPublish(EXCHANGE_NAME, "warning", null, body.getBytes());

        System.out.println("body发送成功: " + body );

        // 7、释放资源
        channel.close();
        connection.close();

    }
}
```

#### 运行效果

新创建的交换机如图所示

![image-20241011140558047](【尚硅谷】RabbitMQ\6708c046f1a9e.png)

详情如图所示，可以看到绑定了两个消息队列`direct_queue1` 和`direct_queue2`，`direct_queue1`关联`error`一个路由键，`direct_queue2`关联了`error`、`info`、`warning`三个路由键

![image-20241011140653263](【尚硅谷】RabbitMQ\6708c07e170b5.png)

### 消费者代码

#### 编写代码

**Consumer1：**

```java
package com.shiguang.rabbitmq.routing;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 13:49
 */
public class Consumer1 {
    static final String QUEUE_NAME = "direct_queue1";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer1 Body: " + new String(body));
                System.out.println("队列1 消费者1 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
```

**Consumer2：**

```java
package com.shiguang.rabbitmq.routing;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 13:49
 */
public class Consumer2 {
    static final String QUEUE_NAME = "direct_queue2";
    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer2 Body: " + new String(body));
                System.out.println("队列2 消费者2 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```

#### 执行效果

由于我们只往`warning`路由键发送消息，而 `direct_queue1`关联`error`一个路由键，`direct_queue2`关联了`error`、`info`、`warning`三个路由键，所以`Consumer1`收不到消息， `Consumer2`可以收到消息

Consumer1：

![image-20241011141728610](【尚硅谷】RabbitMQ\6708c2f953c68.png)

Consumer2：

![image-20241011141757516](【尚硅谷】RabbitMQ\6708c31643ee0.png)

我们可以修改为往`error`路由键发送消息，这样两个消费者就都能接收到消息了

```java
String body = "日志信息: 张三调用了delete方法. 执行出错,日志级别error";
channel.basicPublish(EXCHANGE_NAME, "error", null, body.getBytes());
```

Consumer1：

![image-20241011142312968](【尚硅谷】RabbitMQ\6708c451a5e53.png)

Consumer2：

![image-20241011142342393](【尚硅谷】RabbitMQ\6708c46f0b97a.png)

## 主题模式（Topics）

- Topic类型与Direct相比，都是可以根据RoutingKey把消息路由到不同的队列。只不过Topic类型Exchange可以让队列在绑定Routing key的时候使用通配符
- Routingkey一般都是由一个或多个单词组成，多个单词之间以"`.`"分割，例如：`item.insert`
- 通配符规则：
  - #: 匹配零个或多个词
  - *: 匹配一个词、

![image-20241011142915903](【尚硅谷】RabbitMQ\6708c5bc94a7b.png)



假设有一个主题交换机 `logs`，并且有以下队列和绑定：

- 队列 `critical_errors` 绑定键为 `*.error`
- 队列 `user_logs` 绑定键为 `user.*`
- 队列 `all_logs` 绑定键为 `#`

如果生产者发送一条路由键为 `user.info` 的消息，那么这条消息将被路由到 `user_logs` 和 `all_logs` 队列。

如果生产者发送一条路由键为 `system.error` 的消息，那么这条消息将被路由到 `critical_errors` 和 `all_logs` 队列。

![image-20241011152112193](【尚硅谷】RabbitMQ\6708d1e8e698f.png)

### 生产者代码

#### 编写代码

```java
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

//        body = "[所在系统：goods][日志级别：info][日志内容: 商品发布成功]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
//        System.out.println("body发送成功: " + body );
//
//        body = "[所在系统：goods][日志级别：error][日志内容: 商品发布失败]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body发送成功: " + body );

        // 7、释放资源
        channel.close();
        connection.close();

    }
}
```



#### 执行效果

创建的交换机信息如图所示

![image-20241011145223731](【尚硅谷】RabbitMQ\6708cb287c4af.png)

创建的消息队列如图所示：

![image-20241011145131503](【尚硅谷】RabbitMQ\6708caf44daca.png)

### 消费者代码

#### 编写代码

**Consumer1：**

```java
package com.shiguang.rabbitmq.topic;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 14:36
 */
public class Consumer1 {
    static final String QUEUE_NAME = "topic_queue1";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer1 Body: " + new String(body));
                System.out.println("队列1 消费者1 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
```



**Consumer2：**

```java
package com.shiguang.rabbitmq.topic;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 14:36
 */
public class Consumer2 {
    static final String QUEUE_NAME = "topic_queue2";
    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer2 Body: " + new String(body));
                System.out.println("队列2 消费者2 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```



#### 执行效果

`topic_queue1`匹配规则满足：所有error级别日志存入数据库,所有order系统的日志存入数据库

`topic_queue2`则匹配所有消息

```java
channel.queueBind(queue1Name, EXCHANGE_NAME, "#.error");
channel.queueBind(queue1Name, EXCHANGE_NAME, "order.*");
channel.queueBind(queue2Name, EXCHANGE_NAME, "*.*");
```

我们先发送`order.info`规则的消息，执行并查看效果

```java
// 6、发送消息
// 分别发送消息到队列: order.info、goods.info、goods.error
String body = "[所在系统：order][日志级别：info][日志内容: 订单生成,保存成功]";
channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
System.out.println("body发送成功: " + body );

//        body = "[所在系统：goods][日志级别：info][日志内容: 商品发布成功]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
//        System.out.println("body发送成功: " + body );
//
//        body = "[所在系统：goods][日志级别：error][日志内容: 商品发布失败]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body发送成功: " + body );
```

由于`topic_queue1`与`topic_queue2`均能匹配`order.info`规则，所以`Consumer1`与`Consumer2`均能接收到消息。

Consumer1：

![image-20241011145503121](【尚硅谷】RabbitMQ\6708cbc7c3e9b.png)

Consumer2：

![image-20241011145532452](【尚硅谷】RabbitMQ\6708cbe52a6da.png)

我们再发送`goods.info`这个规则的消息，清空Consumer日志，重新发送消息

```java
// 6、发送消息
// 分别发送消息到队列: order.info、goods.info、goods.error
String body = "[所在系统：order][日志级别：info][日志内容: 订单生成,保存成功]";
channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
System.out.println("body发送成功: " + body );

body = "[所在系统：goods][日志级别：info][日志内容: 商品发布成功]";
channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
System.out.println("body发送成功: " + body );
//
//        body = "[所在系统：goods][日志级别：error][日志内容: 商品发布失败]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body发送成功: " + body );
```

由于`topic_queue1`不能匹配`goods.info`规则，所以`Consumer1`只接收到一条消息，`Consumer2`接收到两条消息。

Consumer1：

![image-20241011150647279](【尚硅谷】RabbitMQ\6708ce87df55f.png)

Consumer2：

![image-20241011150733592](【尚硅谷】RabbitMQ\6708ceb642782.png)

我们继续追加`goods.error`这个规则的消息

```java
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
```

同理可知`Consumer1`只接收到两条消息，`Consumer2`接收到三条消息。

Consumer1：

![image-20241011151629375](【尚硅谷】RabbitMQ\6708d0ce0861b.png)

Consumer2：

![image-20241011151643198](【尚硅谷】RabbitMQ\6708d0dbd51f1.png)

## 远程过程调用（RPC）

- 远程过程调用，本质上是同步调用，和我们使用OpenFeign调用远程接口一样
- 所以这不是典型的消息队列工作方式，我们不展开说明

![image-20241011152301703](【尚硅谷】RabbitMQ\6708d2565e857.png)



## 工作模式小结

直接发送到队列：底层使用了默认交换机

经过交换机发送到队列

- Fanout: 没有Routing key直接绑定队列
- Direct: 通过Routing key绑定队列，消息发送到绑定的队列上
  - 一个交换机绑定一个队列：定点发送
  - 一个交换机绑定多个队列：广播发送
- Topic: 针对Routing key使用通配符



# Spring Boot 整合RabbitMQ

## 基本思路

- 搭建环境
- 基础设定：交换机名称、队列名称、绑定关系
- 发送消息：使用`RabbitTemplate`
- 接收消息：使用`@RabbitListener`注解

## 消费者操作步骤

### 创建项目并导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>module03-springboot-consumer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```

### 创建配置文件

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
logging:
  level:
    com.shiguang.mq.listener.MyMessageListener: info
```

### 创建启动类

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQConsumerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQConsumerMainType.class, args);
    }
}
```

### MyMessageListener

```java
package com.shiguang.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";

    // 写法一: 监听 + 在 RabbitMQ 服务器上创建交换机、队列、绑定关系
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = QUEUE_NAME, durable = "true"),
//            exchange = @Exchange(value = EXCHANGE_DIRECT),
//            key = {ROUTING_KEY}
//    ))
//    public void processMessage(String dataString, Message message, Channel channel) {
//        log.info("消费端接收到消息：{}", dataString);
//        System.out.println("消费端接收到消息：" + dataString);
//    }

    // 写法二: 只监听
    @RabbitListener(queues = QUEUE_NAME)
    public void processMessage(String dataString, Message message, Channel channel) {
        log.info("消费端接收到消息：{}", dataString);
        System.out.println("消费端接收到消息：" + dataString);
    }
}
```

### 测试

启动服务，登录RabbitMQ管理界面查看交换机，消息队列是否创建成功并建立绑定关系

**交换机：**

![image-20241011163324060](【尚硅谷】RabbitMQ\6708e2d4c1531.png)

**消息队列：**

![image-20241011163402237](【尚硅谷】RabbitMQ\6708e2fad2e01.png)

**绑定关系：**

![image-20241011163446266](【尚硅谷】RabbitMQ\6708e326ea4f9.png)

### 图形化界面操作

**创建交换机：**

![image-20241011163749986](【尚硅谷】RabbitMQ\6708e3deac9fd.png)

**创建消息队列：**

![image-20241011163909201](【尚硅谷】RabbitMQ\6708e42df31c6.png)

**建立绑定关系：**

![image-20241011164049337](【尚硅谷】RabbitMQ\6708e49205b0a.png)

添加后如下：

![image-20241011164111141](【尚硅谷】RabbitMQ\6708e4a7aeefd.png)

## 生产者操作步骤

### 创建项目并导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>modul04-springboot-producer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```

### 创建配置文件

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
```

创建启动类

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQProducerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQProducerMainType.class, args);
    }
}
```

创建测试类

> 注意测试类包路径应与项目启动类所属包路径一致，否则@Autowired无法自动装配

```java
package com.shiguang.mq;

/**
 * Created By Shiguang On 2024/10/11 16:49
 */

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01SendMessage(){
        String message = "Hello Rabbit!!";
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    }

}
```

### 测试

执行测试代码，查看后台监控，有一条消息待消费

![image-20241011191042896](【尚硅谷】RabbitMQ\670907b38d80e.png)

启动消费者服务进行消费

![image-20241011191209907](【尚硅谷】RabbitMQ\6709080a8d4ab.png)

# 消息可靠性投递

## 问题场景及解决方案

### 问题场景

下单操作的正常流程如下图所示

![image-20241011191734902](【尚硅谷】RabbitMQ\6709094f6ed58.png)

故障情况1：消息没有发送到消息队列上
后果：消费者拿不到消息，业务功能缺失，数据错误

![image-20241011191841070](【尚硅谷】RabbitMQ\6709099187a59.png)

故障情况2：消息成功存入消息队列，但是消息队列服务器宕机了
原本保存在**<font color = 'red'>内存中的消息</font>**也**<font color = 'red'>丢失</font>**了
即使服务器重新启动，消息也找不回来了
后果：消费者拿不到消息，业务功能缺失，数据错误

![image-20241011191929644](【尚硅谷】RabbitMQ\670909c226d6e.png)

故障情况3：消息成功存入消息队列，但是消费端出现问题，例如：宕机、抛异常等等

后果：业务功能缺失，数据错误

![image-20241011192236717](【尚硅谷】RabbitMQ\67090a7d3dbb9.png)

### 解决方案

故障情况1：消息没有发送到消息队列

- 解决思路A：在**生产者端**进行确认，具体操作中我们会分别针对**交换机**和**队列**来确认
  如果没有成功发送到消息队列服务器上，那就可以尝试重新发送
- 解决思路B：为目标交换机指定**备份交换机**，当目标交换机投递失败时，把消息投递至
  备份交换机

故障情况2：消息队列服务器宕机导致内存中消息丢失

- 解决思路：**消息持久化**到硬盘上，哪怕服务器重启也不会导致消息丢失

故障情况3：消费端宕机或抛异常导致消息没有成功被消费

- 消费端消费消息**成功**，给服务器返回**ACK信息**，然后消息队列删除该消息
- 消费端消费消息**失败**，给服务器端返回**NACK信息**，同时把消息恢复为**待消费**的状态，
  这样就可以再次取回消息，**重试**一次（当然，这就需要消费端接口支持幂等性）

## 故障情况1

### 生产者端实现

#### 创建项目并导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>module05-confirm-producer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```



#### 主启动类

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQProducerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQProducerMainType.class, args);
    }
}
```

#### 配置文件

> 注意：publisher-confirm-type和publisher-returns是两个必须要增加的配置，如果没有则本节功能不生效

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
    publisher-confirm-type: CORRELATED #交换机的确认
    publisher-returns: true #队列的确认
logging:
  level:
    com.shiguang.mq.config.MQProducerAckConfig: info
```

#### 配置类

**目标**：首先我们需要声明回调函数来接收RabbitMQ服务器返回的确认信息：

| 方法名            | 方法功能                 | 所属接口        | 接口所属类     |
| ----------------- | ------------------------ | --------------- | -------------- |
| confirm()         | 确认消息是否发送到交换机 | ConfirmCallback | RabbitTemplate |
| returnedMessage() | 确认消息是否发送到队列   | ReturnsCallback | RabbitTemplate |



然后，就是对RabbitTemplate的功能进行增强，因为回调函数所在对象必须设置到RabbitTemplate对象中才能生效
原本RabbitTemplate对象并没有生产者端消息确认的功能，要给它设置对应的组件才可以。
而设置对应的组件，需要调用RabbitTemplate对象下面两个方法：

| 设置组件调用的方法   | 所需对象类型            |
| -------------------- | ----------------------- |
| setConfirmCallback() | ConfirmCallback接口类型 |
| setReturnCallback()  | ReturnCallback:接口类型 |

代码如下：

> ① 要点1
> 加@Component注解，加入IOC容器（@Configuration已经包含了@Component）
> ② 要点2
> 配置类自身实现ConfirmCallback、ReturnCallbacki这两个接口，然后通过this指针把配置类的对象设置到RabbitTemplate对象中。
> 操作封装到了一个专门的void init()方法中。
> 为了保证这个void init()方法在应用启动时被调用，我们使用@PostConstruct注解来修饰这个方法。
> 关于@PostConstruct注解大家可以参照以下说明：
>
> @PostConstruct注解是**java中的一个标准注解**，它用于指定在**对象创建之后立即执行**的方法。当使用依赖注入（如Spring框架）或者其他方式创建对象时，@PostConstruct注解可以确保在对象完全初始化之后，执行相应的方法。
>
> 使用@PostConstructi注解的方法必须满足以下条件：
>
> 1. 方法不能有任何参数
> 2. 方法必须是非静态的
> 3. 方法不能返回任何值。
>
> 当容器实例化一个带有@PostConstruct注解的Bean时，它会在**调用构造函数之后**，并在**依赖注入完成之前**调用被@PostConstruct注解标记的方法。这样，我们可以在该方法中进行一些初始化操作，比如读取配置文件、建立数据库连接等。

```java
package com.shiguang.mq.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created By Shiguang On 2024/10/11 19:48
 */

@Configuration
@Slf4j
public class RabbitConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 消息发送到交换机成功或失败时调用这个方法
     *
     * @param correlationData 用于关联消息的唯一标识符
     * @param ack             表示消息是否被成功确认
     * @param cause           如果消息确认失败，这里会包含失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("confirm() 回调函数打印 CorrelationData: " + correlationData);
        log.info("confirm() 回调函数打印 ack: " + ack);
        log.info("confirm() 回调函数打印 cause: " + cause);
    }

    /**
     * 当消息无法路由到队列时调用这个方法
     *
     * @param returnedMessage 包含无法路由的消息的详细信息
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage() 回调函数 消息主体: " + new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数 应答码: " + returnedMessage.getReplyCode());
        log.info("returnedMessage() 回调函数 描述: " + returnedMessage.getReplyText());
        log.info("returnedMessage() 回调函数 消息使用的交换器 exchange: " + returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数 消息使用的路由键 routing: " + returnedMessage.getRoutingKey());
    }
}
```

#### 测试类

```java
package com.shiguang.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created By Shiguang On 2024/10/11 20:16
 */

@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01SendMessage(){
        String message = "Message Confirm Test !!";
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    }
}
```

#### 测试

正常执行测试代码，查看日志输出，ack为`true`，cause为`null`

![image-20241011202138328](【尚硅谷】RabbitMQ\67091852e853f.png)

调整交换机名称，故意使其发送失败

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
}
```

重新执行并查看日志输出，ack为`false`，cause有相应错误原因

![image-20241011202706175](【尚硅谷】RabbitMQ\6709199aa83f7.png)

调整路由键名称，故意使其无法匹配

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY + "~", message);
}
```

重新执行并查看日志输出，打印了`returnedMessage()`回到函数日志

![image-20241011203140202](【尚硅谷】RabbitMQ\67091aacbb997.png)

### 备份交换机实现

![image-20241011203624907](【尚硅谷】RabbitMQ\67091bc963bbf.png)

**1、创建备份交换机**

类型必须为`fanout`，因为消息从目标交换机转至备份交换机时是没有路由键的，只能通过广播的方式查找队列。

![image-20241011210002157](【尚硅谷】RabbitMQ\6709215299a4b.png)

**2、创建队列**

![image-20241011210325574](【尚硅谷】RabbitMQ\6709221e137cb.png)

**3、交换机绑定队列**

![image-20241011210448331](【尚硅谷】RabbitMQ\67092270c6687.png)

**4、执行目标交换机的备份交换机**

由于交换机创建后参数无法修改，所以需要将原来的目标删除重新创建并执行备份交换机

删除原来的目标交换机：

![image-20241011210908870](【尚硅谷】RabbitMQ\6709237572ecc.png)

重新创建目标交换机：

![image-20241011211146489](【尚硅谷】RabbitMQ\670924133a9ef.png)

队列重新绑定交换机：

![image-20241011211619274](【尚硅谷】RabbitMQ\67092523b71bb.png)

**5、重新执行测试**

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY + "~", message);
}
```

测试结果：ack为`true`

![image-20241011211911885](【尚硅谷】RabbitMQ\670925d05eceb.png)

`queue.test.backup`有一条消息待消费
![image-20241011212032489](【尚硅谷】RabbitMQ\67092620e9231.png)




## 故障情况2

默认情况下，RabbitMQ服务宕机后，消息会丢失吗?

我们手动重启下RabbitMQ服务，然后查看消息消费情况

```bash
docker restart rabbitmq
```

原来有一条消息待消费

![image-20241011212807995](【尚硅谷】RabbitMQ\670927e8711d7.png)

重启后重新查看，发现带消费消息从0条转变为1条，我们并未重新发送消息，但消息并未丢失

![image-20241011213202836](【尚硅谷】RabbitMQ\670928d33cb1e.png)

其实默认情况下，RabbitMQ是支持持久化数据的，重启后会将保存到磁盘的数据重新加载到内存中

我们可以查看下`@RabbitListener` 注解的源码，找到`Queue`这个接口

```java
Queue[] queuesToDeclare() default {};
```

可以看到，`durable()`和 `autoDelete()`虽然默认值都为空，但源码注释中有说明，默认是支持持久化但是并不会自动删除的。

```java
/**
	 * Specifies if this queue should be durable.
	 * By default if queue name is provided it is durable.
	 * @return true if the queue is to be declared as durable.
	 * @see org.springframework.amqp.core.Queue#isDurable()
	 */
String durable() default "";

/**
	 * Specifies if this queue should be auto deleted when not used.
	 * By default if queue name is provided it is not auto-deleted.
	 * @return true if the queue is to be declared as auto-delete.
	 * @see org.springframework.amqp.core.Queue#isAutoDelete()
	 */
String autoDelete() default "";
```

## 故障情况3

### 消费者端实现

#### 创建项目并导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>module06-confirm-consumer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### 主启动类

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQConsumerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQConsumerMainType.class, args);
    }
}
```

#### 配置文件

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual # 把消息确认模式改为手动确认
logging:
  level:
    com.shiguang.mq.listener.MyMessageListener: info
```

#### Listener

> channel.basicNack与channel.basicReject的区别
>
> channel.basicReject(long deliveryTag, boolean requeue)
>
> channel.basicReject比channel.basicNack少了个是否批量操作的参数`multiple`，不能控制是否批量操作

```java
package com.shiguang.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String QUEUE_NAME = "queue.order";

    @RabbitListener(queues = QUEUE_NAME)
    public void processMessage(String dataString, Message message, Channel channel) throws IOException {
        // 获取当前消息的唯一标识
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 核心操作
            log.info("消费端接收到消息：{}", dataString);
            // 核心操作成功,返回 ACK 信息
            // deliveryTag: 消息的唯一标识,64 位的长整型,消息往消费端投递时,会分配一个唯一的 deliveryTag 值
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 获取当前消息是否是重复投递的,true 说明当前消息已经重试过一次了, false 说明当前消息是第一次投递
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            // 核心操作失败,返回 NACK 信息
            // requeue: 是否重新入队,true 表示重新入队, false 表示丢弃
            if (redelivered){
                // 如果当前消息已经是重复投递的,则说明此前已经重试过一次了,则不再重试过了,直接丢弃
                channel.basicNack(deliveryTag, false, false);
            }else {
                // 如果当前消息不是重复投递的,则说明此前没有重试过一次,则重试过一次,重新入队
                channel.basicNack(deliveryTag, false, true);
            }

            throw new RuntimeException(e);
        }
    }
}
```

#### 消息确认相关方法参数说明

**1、delivery Tag: 交付标签机制**
消费端把消息处理结果ACK、NACK、Reject等返回给Broker之后，Broker需要对对应的消息执行后续操作，例如删除消息、重新排队或标记为死信等等。那么Broker就必须知道它现在要操作的消息具体是哪一条。而delivery Tag作为消息的唯一标识就很好的满足了这个需求。



提问：如果交换机是Fanout模式，同一个消息广播到了不同队列，delivery Tag会重复吗？

答：不会，deliveryTag在Broker范围内唯一



思考：更新购物车的微服务消费了消息返回ACK确认信息，然后Broker删除了消息，进而导致更新库存
更新积分的功能拿不到消息一这种情况会发生吗？



**2、multiple: 是否批量处理**

multiple为 `true` 时，采用批量处理

![image-20241011220119070](【尚硅谷】RabbitMQ\67092faf85b10.png)

multiple为`false `时，进行单独处理

![image-20241011220105475](【尚硅谷】RabbitMQ\67092fa1ee2a8.png)

由于批量操作可能导致误操作，所以一般将`multiple` 设为`false`



**3、requeue：是否重新入队**

true 表示重新入队, false 表示丢弃

#### 测试

**1、以Debug模式启动Consumer服务**

**2、在图形化界面生成一条消息**

找到`exchange.direct.order`交换机，然后手动发布一条消息

![image-20241012092733091](【尚硅谷】RabbitMQ\6709d0863d22f.png)

消息发布成功，Debug进入到方法内部

![image-20241012094558111](【尚硅谷】RabbitMQ\6709d4d73a565.png)

**3、再查看`queue.order`队列情况**

发现消息已经被消费尚未ACK确认

![image-20241012095451081](【尚硅谷】RabbitMQ\6709d6ebe5c18.png)

**4、消费端正常放行，返回ACK进行确认**

再次查看队列情况

![image-20241012095857461](【尚硅谷】RabbitMQ\6709d7e249d1c.png)

接下来我们模拟异常场景，修改代码，手动打印 `1/0`使程序出错，重启服务

```java
log.info("消费端接收到消息：{}", dataString);
System.out.println(1/0);
```

**1、重新发布一条消息**

![image-20241012101804618](【尚硅谷】RabbitMQ\6709dc5db05bf.png)

**2、debug逐条执行，观察运行情况**

出现异常被catch捕获，此时 `redelivered `的值为`false`

![image-20241012101747485](【尚硅谷】RabbitMQ\6709dd5b09135.png)

继续执行，方法进入else ，重新放入队列

![image-20241012102307570](【尚硅谷】RabbitMQ\6709dd8c64f7f.png)

放行，此时消息仍是待确认

![image-20241012102420402](【尚硅谷】RabbitMQ\6709ddd54036c.png)

重新进入Debug，继续逐条执行，这次`redelivered `的值为`true`，不再重试，直接丢弃

![image-20241012102542308](【尚硅谷】RabbitMQ\6709de27302dd.png)

放行，此时再查看队列情况

![image-20241012102828818](【尚硅谷】RabbitMQ\6709decda8a13.png)

# 消费端限流

消费端限流可以实现削峰减谷的作用，假设消息总量为1万条，如果一次性取出所有消息会导致消费端并发压力过大，我们可以限制**每次最多**从队列取出1000条消息，这样就可以对消费端进行很好的保护。

![image-20241012103645282](【尚硅谷】RabbitMQ\6709e0be28361.png)

实现也比较简单，只需添加`prefetch`参数即可

先观察下默认情况下是如何处理的

**1、我们重写一个测试方法，生产端发布100条消息**

```java
@Test
public void test02SendMessage() {
    for (int i = 0; i < 100; i++) {
        String message = "Test Rrefetch!!" + i;
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, message);
    }
}
```

消息发布后查看下队列情况

![image-20241012104618929](【尚硅谷】RabbitMQ\6709e2fbbc738.png)

2、消费端Listener注释掉原来的方法，新增一个方法进行处理

```java
@RabbitListener(queues = QUEUE_NAME)
public void processMessage(String dataString, Message message, Channel channel) throws IOException, InterruptedException {
    // 核心操作
    log.info("消费端接收到消息：{}", dataString);

    TimeUnit.SECONDS.sleep(1); //延迟 1 秒

    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

**3、运行消费端服务并查看队列情况**

观察发现 `Ready`数量直接从`100`变为`0`，`Unacked`和`Total`随着消息被消费端消费逐渐减少，说明消费时一次性取出队列中的所有消息，然后逐条消费。

![image-20241012105500366](【尚硅谷】RabbitMQ\6709e50531022.png)

接下来我们限制每次从队列中获取的数量并观察队列运行情况

**1、添加配置，设置每次从队列中获取消息的数量**

```yml
spring:
  rabbitmq:
    listener:
      simple:
        prefetch: 1 # 每次只消费一个消息
```

**2、重新发布消息以及重启消费端服务并观察队列运行情况**

我们可以看到`Ready`数量每次变化减`5`，这是因为图形化界面每`5`秒刷新一次

![image-20241012110618114](【尚硅谷】RabbitMQ\6709e7aaec766.png)

# 消息超时

给消息设定一个过期时间，超过这个时间没有被取走的消息就会被删除
我们可以从两个层面来给消息设定过期时间：

- 队列层面：在队列层面设定消息的过期时间，并不是队列的过期时间。意思是这个队列中的消息全部使用同一个过期时间。
- 消息本身：给具体的某个消息设定过期时间
- 如果两个层面都做了设置，那么哪个时间短，哪个生效

## 测试

### 给队列设置超时时间

**1、创建交换机和队列并建立绑定关系**

交换机：

![image-20241012111226196](【尚硅谷】RabbitMQ\6709e91b00fef.png)

队列：

![image-20241012111351079](【尚硅谷】RabbitMQ\6709e96fdf9ac.png)

交换机绑定队列：

![image-20241012111518137](【尚硅谷】RabbitMQ\6709e9c701640.png)

**2、新增测试方法并执行测试**

```java
public static final String EXCHANGE_TIMEOUT = "exchange.test.timeout";
public static final String ROUTING_KEY_TIMEOUT = "routing.key.test.timeout";

@Test
public void test03SendMessage() {
    String message = "Test Timeout!!";
    rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message);
}
```

此时观察队列情况，发现`Total`数量从`0`变为`1`，而我们并未运行消费端进行消费，这是因为我们给队列设置了过期时间，队列内的消息超出过期时间后被丢弃

![image-20241012112523300](【尚硅谷】RabbitMQ\6709ec2431911.png)

### 给消息设置超时时间

**1、删除原来的队列并重新创建，不设置超时时间**

队列：

![image-20241012113853021](【尚硅谷】RabbitMQ\6709ef4dcab2d.png)

重新绑定：

![image-20241012113922487](【尚硅谷】RabbitMQ\6709ef6b5b631.png)

2、新增测试方法，添加后置处理器对象参数

```java
@Test
public void test04SendMessage() {

    // 创建消息后置处理器对象
    MessagePostProcessor processor = message -> {
        // 设置消息的过期时间为 7 秒
        message.getMessageProperties().setExpiration("7000");
        return message;
    };

    String message = "Test Timeout!!";

    rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message,processor);
}
```

**3、设置`Ack Mode`为`Automatic ack`**

这样消息处理失败不会重新加入队列

![image-20241012114422686](【尚硅谷】RabbitMQ\6709f0979a4aa.png)

**4、执行测试方法并观察队列情况**

消息超出超时时间后被清除

![image-20241012114732505](【尚硅谷】RabbitMQ\6709f15547536.png)

# 死信和死信队列

概念：当一个消息无法被消费，它就变成了死信。
死信产生的原因大致有下面三种：

- 拒绝：消费者拒接消息，basicNack(/basicReject(),并且不把消息重新放入原目标队列，requeue=false
- 溢出：队列中消息数量到达限制。比如队列最大只能存储10条消息，且现在已经存储了10条，此时如果再发送一条消息进来，根据先进先出原则，队列中最早的消息会变成死信
- 超时：消息到达超时时间未被消费

死信的处理方式大致有下面三种：

- 丢弃：对不重要的消息直接丢弃，不做处理
- 入库：把死信写入数据库，日后处理
- 监听：消息变成死信后进入死信队列，我们专门设置消费端监听死信队列，做后续处理（通常采用）

## 测试相关准备

### 创建死信交换机和死信队列

- 死信交换机: `exchange.dead.letter.video`
- 死信队列：`queue.dead.letter.video`
- 死信路由键：`routing.key.dead.letter.video`

### 创建正常交换机和正常队列

> 注意：一定要注意正常队列有诸多限定和设置，这样才能让无法处理的消息进入死信交换机
>
> x-dead-letter-exchange: 关联的死信交换机
>
> x-dead-letter-routing-key：关联的死信路由键
>
> x-max-length：队列最大容量长度
>
> x-message-ttl：队列超时时间

![image-20241012120513217](【尚硅谷】RabbitMQ\6709f57a2079d.png)

- 正常交换机：`exchange.normal.video`
- 正常队列: `queue.normal.video`
- 正常路由键：`routing.key.normal.video`



### java代码中的相关常量声明

```java
public static final String EXCHANGE_NORMAL = "exchange.normal.video";
public static final String EXCHANGE_DEAD_LETTER = "exchange.dead.letter.video";
    
public static final String ROUTING_KEY_NORMAL = "routing.key.normal.video";
public static final String ROUTING_KEY_DEAD_LETTER = "routing.key.dead.letter.video";
    
public static final String QUEUE_NORMAL = "queue.normal.video";
public static final String QUEUE_DEAD_LETTER = "queue.dead.letter.video";
```



## 消费端拒收消息

### 发送消息的代码

> 也可直接在图形化界面操作

```java
@Test
public void testSendRejectMessage() {
    rabbitTemplate.convertAndSend(EXCHANGE_NORMAL, ROUTING_KEY_DEAD_LETTER, "测试死信情况1:消息被拒绝");
}
```

### 接收消息的代码

> 由于监听正常队列的方法一定会拒绝并且不会重新加入队列，那么队列中的消息就会成为死信并加入到死信队列中，死信队列正常返回。

① 监听正常队列

```java
/**
* 监听正常队列
*/
@RabbitListener(queues = QUEUE_NORMAL)
public void processNormalMessage(Message message, Channel channel) throws IOException {
    // 监听正常队列,但是拒绝消息
    log.info("★[normal] 消息接收到,但我拒绝。");
    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
}
```

② 监听死信队列

```java
/**
* 监听死信队列
*/
@RabbitListener(queues = QUEUE_DEAD_LETTER)
public void processDeadMessage(String dataString, Message message, Channel channel) throws IOException {
    //监听死信队列
    log.info("★[dead letter] dataString = " + dataString);
    log.info("★[dead1 etter] 我是死信监听方法,我接收到了死信消息");
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

### 执行结果

**1、正常队列发布消息**

![image-20241012124002121](【尚硅谷】RabbitMQ\6709fda2dfd30.png)

**2、重启消费端服务**

后台日志输出情况

![image-20241012124439157](【尚硅谷】RabbitMQ\6709feb7e0e1a.png)

**3、观察队列情况**

正常队列：

![image-20241012124716010](【尚硅谷】RabbitMQ\6709ff54bc1cb.png)

死信队列：

![image-20241012124709660](【尚硅谷】RabbitMQ\6709ff4e73188.png)

## 消费数量超过队列容量极限

### 发送消息的代码

```java
@Test
public void testSendMultiMessage() {
    for (int i = 0; i < 20; i++) {
        rabbitTemplate.
            convertAndSend(
            EXCHANGE_NORMAL,
            ROUTING_KEY_NORMAL,
            "测试死信情况2:数量超过队列最大容量" + i);
    }
}
```

### 接收消息的代码

### 执行效果

**1、停止消费端服务，批量发送20条消息**

**2、观察队列情况**

正常队列：

由于我们设置的最容量为`10`，所以我们最多接收`10`条消息，超出设定的超时时间后消息被废弃，数量变为`0`

![image-20241012125548969](【尚硅谷】RabbitMQ\670a0155bd920.png)

死信队列：

由于我们设置的最大容量为`10`，消息成为死信后每`10`条消息为一个批次加入死信队列

![image-20241012125605016](【尚硅谷】RabbitMQ\670a0165cecc9.png)

此时我们启动消费端服务，观察日志输出情况，可以发现都是`dead`级别的日志，因为此时队列里的所有消息都变为死信了。

![image-20241012130701315](【尚硅谷】RabbitMQ\670a03f652392.png)

## 消息超时未消费

### 发送消息的代码

> 由于我们设置的队列最大容量为10，为了避免由于溢出产生死信的影响，我们发送小于10条的数据

```java
@Test
public void testSendDelayMessage() {
    for (int i = 0; i < 8; i++) {
        rabbitTemplate.
            convertAndSend(
            EXCHANGE_NORMAL,
            ROUTING_KEY_NORMAL,
            "测试死信情况3:消息超时未消费" + i);
    }
}
```

### 执行效果

**1、停止消费端服务，发送消息**

**2、查看队列情况**

正常队列：

![image-20241012133737142](【尚硅谷】RabbitMQ\670a0b21d5eb5.png)

死信队列：

死信队列从原始的`30`条数量增至`38`条，我们发送的`8`条数据因为超时未消费加入到死信队列中

![image-20241012133715332](【尚硅谷】RabbitMQ\670a0b0c29ff9.png)

# 延迟队列

## 业务场景

在限定时间内进行支付，否则订单自动取消

![image-20241012134202272](【尚硅谷】RabbitMQ\670a0c2b08952.png)

## 实现思路

### **方案1：设置消息超时时间 + 死信队列**

> 可参考上文介绍，不再演示

![image-20241012134438561](【尚硅谷】RabbitMQ\670a0cc76665e.png)

### **方案2：给RabbitMQ安装插件**

#### 插件介绍

官网地址：https:/github.com/rabbitmq/rabbitmq-delayed-message-exchange
延迟极限：最多两天

#### 安装插件

#### 确定卷映射目录

```shell
docker inspect rabbitmq
```

运行结果：

```json
[
    {
        "Id": "3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e",
        "Created": "2024-10-10T14:41:39.651931938Z",
        "Path": "docker-entrypoint.sh",
        "Args": [
            "rabbitmq-server"
        ],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,
            "OOMKilled": false,
            "Dead": false,
            "Pid": 2671,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2024-10-12T01:18:16.845798068Z",
            "FinishedAt": "2024-10-12T01:15:50.852558669Z"
        },
        "Image": "sha256:c7383e9ad93d65dea7219907c8ac08e6f8cdad481f17c78b3864f29b2cd50a7b",
        "ResolvConfPath": "/var/lib/docker/containers/3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e/resolv.conf",
        "HostnamePath": "/var/lib/docker/containers/3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e/hostname",
        "HostsPath": "/var/lib/docker/containers/3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e/hosts",
        "LogPath": "/var/lib/docker/containers/3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e/3767efc3f46e05b63dbb6a244f2f5a850a60febe52cc1bdf96e75f5449d7979e-json.log",
        "Name": "/rabbitmq",
        "RestartCount": 0,
        "Driver": "overlay2",
        "Platform": "linux",
        "MountLabel": "",
        "ProcessLabel": "",
        "AppArmorProfile": "",
        "ExecIDs": null,
        "HostConfig": {
            "Binds": [
                "rabbitmq-plugin:/plugins"
            ],
            "ContainerIDFile": "",
            "LogConfig": {
                "Type": "json-file",
                "Config": {}
            },
            "NetworkMode": "bridge",
            "PortBindings": {
                "15672/tcp": [
                    {
                        "HostIp": "",
                        "HostPort": "15672"
                    }
                ],
                "5672/tcp": [
                    {
                        "HostIp": "",
                        "HostPort": "5672"
                    }
                ]
            },
            "RestartPolicy": {
                "Name": "no",
                "MaximumRetryCount": 0
            },
            "AutoRemove": false,
            "VolumeDriver": "",
            "VolumesFrom": null,
            "ConsoleSize": [
                49,
                108
            ],
            "CapAdd": null,
            "CapDrop": null,
            "CgroupnsMode": "host",
            "Dns": [],
            "DnsOptions": [],
            "DnsSearch": [],
            "ExtraHosts": null,
            "GroupAdd": null,
            "IpcMode": "private",
            "Cgroup": "",
            "Links": null,
            "OomScoreAdj": 0,
            "PidMode": "",
            "Privileged": false,
            "PublishAllPorts": false,
            "ReadonlyRootfs": false,
            "SecurityOpt": null,
            "UTSMode": "",
            "UsernsMode": "",
            "ShmSize": 67108864,
            "Runtime": "runc",
            "Isolation": "",
            "CpuShares": 0,
            "Memory": 0,
            "NanoCpus": 0,
            "CgroupParent": "",
            "BlkioWeight": 0,
            "BlkioWeightDevice": [],
            "BlkioDeviceReadBps": [],
            "BlkioDeviceWriteBps": [],
            "BlkioDeviceReadIOps": [],
            "BlkioDeviceWriteIOps": [],
            "CpuPeriod": 0,
            "CpuQuota": 0,
            "CpuRealtimePeriod": 0,
            "CpuRealtimeRuntime": 0,
            "CpusetCpus": "",
            "CpusetMems": "",
            "Devices": [],
            "DeviceCgroupRules": null,
            "DeviceRequests": null,
            "MemoryReservation": 0,
            "MemorySwap": 0,
            "MemorySwappiness": null,
            "OomKillDisable": false,
            "PidsLimit": null,
            "Ulimits": [],
            "CpuCount": 0,
            "CpuPercent": 0,
            "IOMaximumIOps": 0,
            "IOMaximumBandwidth": 0,
            "MaskedPaths": [
                "/proc/asound",
                "/proc/acpi",
                "/proc/kcore",
                "/proc/keys",
                "/proc/latency_stats",
                "/proc/timer_list",
                "/proc/timer_stats",
                "/proc/sched_debug",
                "/proc/scsi",
                "/sys/firmware",
                "/sys/devices/virtual/powercap"
            ],
            "ReadonlyPaths": [
                "/proc/bus",
                "/proc/fs",
                "/proc/irq",
                "/proc/sys",
                "/proc/sysrq-trigger"
            ]
        },
        "GraphDriver": {
            "Data": {
                "LowerDir": "/var/lib/docker/overlay2/7f9ec2fa1e82857b9f69c15ff993393a2787d8b854cd0b8a56ac6131ec7e6fb2-init/diff:/var/lib/docker/overlay2/cdd788016ee61771c380142548344cbed891addecfd97646c4cf42d9edd3ce8c/diff:/var/lib/docker/overlay2/0b656bd93fa5cdda1adac4843dc83a1d08cf0af5bb45c5a2b73aafed4f90838e/diff:/var/lib/docker/overlay2/6252d4ba56e7b90f4d9e87bf441483853dcefb58e49784cfedfe67e8a48d8d79/diff:/var/lib/docker/overlay2/3383c7042c8fba359d23128aa2c41964e30a96c18e7c3db2f7032dfe17399201/diff:/var/lib/docker/overlay2/78a8fa92f9e0114da9aa6e61acd4977c8a9b954a669bfb2aa90419923573f4da/diff:/var/lib/docker/overlay2/cff69ece62be74cc51d8bbef3742b39f6cc400c7ee3f24058a7a0527e6827d3a/diff:/var/lib/docker/overlay2/8cabb7d5fb5e7367ad9b66f8e17fd900ee3ef0314b2688a2934e780946484861/diff:/var/lib/docker/overlay2/845a32b37870732f9007b1be2e7ab61e6df0bd6292b1fc5198f4306c623b2ab1/diff:/var/lib/docker/overlay2/69d0a01812c1cd2d1f040967b9d0a7a2d79c3ef10413e992762079b9a2ad5b2d/diff:/var/lib/docker/overlay2/e641dae2802f486d2f4b0f8f29b81903470684e403dd74ced36e0146be9a34ea/diff",
                "MergedDir": "/var/lib/docker/overlay2/7f9ec2fa1e82857b9f69c15ff993393a2787d8b854cd0b8a56ac6131ec7e6fb2/merged",
                "UpperDir": "/var/lib/docker/overlay2/7f9ec2fa1e82857b9f69c15ff993393a2787d8b854cd0b8a56ac6131ec7e6fb2/diff",
                "WorkDir": "/var/lib/docker/overlay2/7f9ec2fa1e82857b9f69c15ff993393a2787d8b854cd0b8a56ac6131ec7e6fb2/work"
            },
            "Name": "overlay2"
        },
        "Mounts": [
            {
                "Type": "volume",
                "Name": "rabbitmq-plugin",
                "Source": "/var/lib/docker/volumes/rabbitmq-plugin/_data",
                "Destination": "/plugins",
                "Driver": "local",
                "Mode": "z",
                "RW": true,
                "Propagation": ""
            },
            {
                "Type": "volume",
                "Name": "b7b13350e8b0d3596aff94385354a1b9366dffeb6b38f8e82a519638f22d74a0",
                "Source": "/var/lib/docker/volumes/b7b13350e8b0d3596aff94385354a1b9366dffeb6b38f8e82a519638f22d74a0/_data",
                "Destination": "/var/lib/rabbitmq",
                "Driver": "local",
                "Mode": "",
                "RW": true,
                "Propagation": ""
            }
        ],
        "Config": {
            "Hostname": "3767efc3f46e",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "ExposedPorts": {
                "15671/tcp": {},
                "15672/tcp": {},
                "15691/tcp": {},
                "15692/tcp": {},
                "25672/tcp": {},
                "4369/tcp": {},
                "5671/tcp": {},
                "5672/tcp": {}
            },
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "RABBITMQ_DEFAULT_USER=guest",
                "RABBITMQ_DEFAULT_PASS=123456",
                "PATH=/opt/rabbitmq/sbin:/opt/erlang/bin:/opt/openssl/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "ERLANG_INSTALL_PATH_PREFIX=/opt/erlang",
                "OPENSSL_INSTALL_PATH_PREFIX=/opt/openssl",
                "RABBITMQ_DATA_DIR=/var/lib/rabbitmq",
                "RABBITMQ_VERSION=3.13.7",
                "RABBITMQ_PGP_KEY_ID=0x0A9AF2115F4687BD29803A206B73A36E6026DFCA",
                "RABBITMQ_HOME=/opt/rabbitmq",
                "HOME=/var/lib/rabbitmq",
                "LANG=C.UTF-8",
                "LANGUAGE=C.UTF-8",
                "LC_ALL=C.UTF-8"
            ],
            "Cmd": [
                "rabbitmq-server"
            ],
            "Image": "rabbitmq:3.13-management",
            "Volumes": {
                "/var/lib/rabbitmq": {}
            },
            "WorkingDir": "",
            "Entrypoint": [
                "docker-entrypoint.sh"
            ],
            "OnBuild": null,
            "Labels": {
                "org.opencontainers.image.ref.name": "ubuntu",
                "org.opencontainers.image.version": "22.04"
            }
        },
        "NetworkSettings": {
            "Bridge": "",
            "SandboxID": "8e3bdc85876ee83c4dc6f9e6501e1cdf6a2f6eba255424d3b541ca4043ff6f91",
            "SandboxKey": "/var/run/docker/netns/8e3bdc85876e",
            "Ports": {
                "15671/tcp": null,
                "15672/tcp": [
                    {
                        "HostIp": "0.0.0.0",
                        "HostPort": "15672"
                    },
                    {
                        "HostIp": "::",
                        "HostPort": "15672"
                    }
                ],
                "15691/tcp": null,
                "15692/tcp": null,
                "25672/tcp": null,
                "4369/tcp": null,
                "5671/tcp": null,
                "5672/tcp": [
                    {
                        "HostIp": "0.0.0.0",
                        "HostPort": "5672"
                    },
                    {
                        "HostIp": "::",
                        "HostPort": "5672"
                    }
                ]
            },
            "HairpinMode": false,
            "LinkLocalIPv6Address": "",
            "LinkLocalIPv6PrefixLen": 0,
            "SecondaryIPAddresses": null,
            "SecondaryIPv6Addresses": null,
            "EndpointID": "6fd5e5f59233ec528be7df6e5f500d800b7abb4df049f2576bb92c5b859d3137",
            "Gateway": "172.17.0.1",
            "GlobalIPv6Address": "",
            "GlobalIPv6PrefixLen": 0,
            "IPAddress": "172.17.0.2",
            "IPPrefixLen": 16,
            "IPv6Gateway": "",
            "MacAddress": "02:42:ac:11:00:02",
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "MacAddress": "02:42:ac:11:00:02",
                    "NetworkID": "7cba32bdc71b92580e2873585313c97476d61b466b33335116931c7f3b7dbb8b",
                    "EndpointID": "6fd5e5f59233ec528be7df6e5f500d800b7abb4df049f2576bb92c5b859d3137",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "DriverOpts": null,
                    "DNSNames": null
                }
            }
        }
    }
]
```

查看`Mounts`中Name为`rabbitmq-plugin`对应的`Source`值

可以看到值为`/var/lib/docker/volumes/rabbitmq-plugin/_data`

```json 
"Mounts": [
    {
        "Type": "volume",
        "Name": "rabbitmq-plugin",
        "Source": "/var/lib/docker/volumes/rabbitmq-plugin/_data",
        "Destination": "/plugins",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    },
    {
        "Type": "volume",
        "Name": "b7b13350e8b0d3596aff94385354a1b9366dffeb6b38f8e82a519638f22d74a0",
        "Source": "/var/lib/docker/volumes/b7b13350e8b0d3596aff94385354a1b9366dffeb6b38f8e82a519638f22d74a0/_data",
        "Destination": "/var/lib/rabbitmq",
        "Driver": "local",
        "Mode": "",
        "RW": true,
        "Propagation": ""
    }
]
```

#### 下载延迟插件

官方文档说明页地址：https://www.rabbitmq.com/community-plugins

**rabbitmq_delayed_message_exchange**

A plugin that adds delayed-messaging (or scheduled-messaging) to RabbitMQ.

- [Releases](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases)
- Author: **Alvaro Videla**
- GitHub: [rabbitmq/rabbitmq-delayed-message-exchange](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)



下载插件安装文件：

```shell
cd /var/lib/docker/volumes/rabbitmq-plugin/_data
wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez
```

若连接被拒绝可多次尝试，或手动下载

![image-20241012141305073](【尚硅谷】RabbitMQ\670a1371eaf32.png)

### 启用插件

```shell
# 登录进入容器内部
docker exec -it rabbitmq /bin/bash

# rabbitmq-plugins命令所在目录已经配置到$PATH环境变量中了,可以直接调用
rabbitmq-plugins enable rabbitmq_delayed_message_exchange

# 查看插件列表，检查插件是否启用 有E*标识即为已启用
# [E*] rabbitmq_delayed_message_exchange 3.13.0
rabbitmq-plugins list

# 退出Docker容器
exit

# 重启Docker容器
docker restart rabbitmq
```



#### 确认

确认点1：查看当前节点已启用插件的列表：

![image-20241012142358715](【尚硅谷】RabbitMQ\670a15ff657d8.png)

确认点2：如果创建新交换机时在`Type`中可以看到`x-delayed-message`选项，则说明插件安装好了

![image-20241012143937188](【尚硅谷】RabbitMQ\670a19aadf4f8.png)

### 创建交换机及队列

**创建交换机：**

Type选择`x-delayed-message`，添加`x-delayed-type`来指定交换机类型

![image-20241012142648189](【尚硅谷】RabbitMQ\670a16a8d31da.png)

**创建队列：**

![image-20241012142901241](【尚硅谷】RabbitMQ\670a172def313.png)

**队列绑定交换机：**

![image-20241012143149354](【尚硅谷】RabbitMQ\670a17d60254c.png)

### 代码测试

#### 生产者端代码

```java
public static final String EXCHANGE_DELAY = "exchange.test.delay";
public static final String ROUTING_KEY_DELAY = "routing.key.test.delay";

@Test
public void sendDelayMessageByPlugin() {
    // 创建消息后置处理器对象
    MessagePostProcessor processor = message -> {
        // x-delay: 消息的过期时间 (单位:毫秒)
        // 安装 rabbitmq_delayed_message_exchange 插件才生效
        message.getMessageProperties().setHeader("x-delay", 10000);
        return message;
    };

    rabbitTemplate.
        convertAndSend(
        EXCHANGE_DELAY,
        ROUTING_KEY_DELAY,
        "Test Delay Message By Plugin" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
        processor);
}
```

#### 消费者端代码

```java
public static final String QUEUE_DELAY = "queue.test.delay";

@RabbitListener(queues = {QUEUE_DELAY})
public void processDelayMessage(String dataString, Message message, Channel channel) throws IOException {
    //监听死信队列
    log.info("[delay message] [消息本身] " + dataString);
    log.info("[delay message] [当前时间] " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

启动消费者端服务并发送消息，查看日志输出情况

![image-20241012150040029](【尚硅谷】RabbitMQ\670a1e98aa942.png)

注意：启用插件后，returnedMessage方法始终会执行

![image-20241012150258397](【尚硅谷】RabbitMQ\670a1f2319d8d.png)

# 事务消息

> RabbitMQ的事务只是作用到生产者端，而且只起到局部作用

RabbitMQ的事务功能非常有限，只是控制是否将缓存中的消息发送到Broker，并不能保证消息的可靠性投递

![image-20241012151455965](【尚硅谷】RabbitMQ\670a21f0d0051.png)

## 实操演示

### 环境准备

#### 创建项目并导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>module07-tx-producer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```

#### 配置文件

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
logging:
  level:
    com.shiguang.mq: info
```

#### 启动类

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/12 15:34
 */
@SpringBootApplication
public class RabbitMQProducerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQProducerMainType.class,args);
    }
}
```

#### 配置类

```java
package com.shiguang.mq.config;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created By Shiguang On 2024/10/12 15:27
 */
@Configuration
@Data
public class RabbitConfig {
    @Bean
    public RabbitTransactionManager transactionManager(CachingConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }


}
```

#### 测试类

```java
package com.shiguang.mq;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created By Shiguang On 2024/10/12 15:36
 */
@SpringBootTest
@Slf4j
public class RabbitMQTest {
    public static final String EXCHANGE_NAME = "exchange.tx.dragon";
    public static final String ROUTING_KEY = "routing.key.tx.dragon";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessageTx(){
        // 1、 发送一条消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 1");

        // 2、抛出异常
        log.info("do bad: "+ 10/0);

        // 3、发送第二条消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 2");
    }
}
```



### 测试

> 我们分别发送两条消息，两条消息中间手动抛出异常，来观察启用事务前后的区别

**1、创建交换机、队列并绑定关系**

交换机名称：exchange.tx.dragon

队列名称：queue.test.tx

路由键：routing.key.tx.dragon

**2、发送消息并观察队列情况**

默认未使用事务的情况：第一条事务发送成功，消息能够正常获取



![image-20241012155135362](【尚硅谷】RabbitMQ\670a2a8802691.png)

开启事务：

测试类添加`@Transactional`注解，由于JUnit中是默认回滚的，我们想要提交事务，需要添加`@Rollback(value = false)`注解

```java
@Test
@Transactional
//@Rollback(value = false)
public void testSendMessageTx(){
    // 1、 发送一条消息
    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 1");

    // 2、抛出异常
    log.info("do bad: "+ 10/0);

    // 3、发送第二条消息
    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 2");
}
```

我们保持默认回滚事务，执行测试方法，观察队列情况

由于出现异常，事务被回滚，消息未发送

![image-20241012160804246](【尚硅谷】RabbitMQ\670a2e64e3dbc.png)

# 惰性队列

惰性队列：未设置惰性模式时队列的持久化机制

创建队列时，在Durabilityi这里有两个选项可以选择

- Durable: 持久化队列，消息会持久化到硬盘上
- Transient: 临时队列，不做持久化操作，broker重启后消息会丢失

![image-20241012161104810](【尚硅谷】RabbitMQ\670a2f1969856.png)

> 思考：Durable队列在存入消息之后，是否是立即保存到硬盘呢？

其实并不会立即保存到硬盘，当内存中的队列达到一定容量或者Broker关闭时才会保存到硬盘

![image-20241012161258471](【尚硅谷】RabbitMQ\670a2f8b26856.png)



官网上对于惰性队列的介绍

![image-20241012161555727](【尚硅谷】RabbitMQ\670a303c6f78a.png)



比较下面两个说法是否是相同的意思：

- 立即移动到硬盘
- 尽早移动到硬盘

理解：

- 立即：消息刚进入队列时

- 尽早：服务器不繁忙时

  

惰性队列应用场景

![image-20241012161831032](【尚硅谷】RabbitMQ\670a30d7b419f.png)

原文翻译：使用惰性队列的主要原因之一是支持非常长的队列（数百万条消息）
由于各种原因，排队可能会变得很长：

- 消费者离线/崩溃/停机进行维护
- 突然出现消息进入高峰生产者的速度超过了消费者
- 消费者比正常情况慢

# 优先级队列

机制说明
默认情况：基于队列先进先出的特性，通常来说，先入队的先投递
设置优先级之后：优先级高的消息更大几率先投递
关键参数：`x-max-priority`

RabbitMQ允许我们使用一个正整数给消息设定优先级
消息的优先级数值取值范围：`1~255`
RabbitMQ官网建议在`1~5`之间设置消息的优先级（优先级越高，占用CPU、内存等资源越多)

队列在声明时可以指定参数：`x-max-priority`
默认值：`0` ，此时消息即使设置优先级也无效
指定一个正整数值：消息的优先级数值不能超过这个值

## 实操演示

**1、创建交换机及队列并绑定**

交换机名称：exchange.test.priority

队列名称：queue.test.priority

> x-max-priority的类型必须是Number

![image-20241012163146628](【尚硅谷】RabbitMQ\670a33f343577.png)

路由键：routing.key.test.priority

**2、分别发送三条消息，优先级从低到高，后面观察入队情况**

```java
public static final String EXCHANGE_PRIORITY = "exchange.test.priority";
public static final String ROUTING_KEY_PRIORITY = "routing.key.test.priority";
```

发送第一条消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 1",message -> {
            //消息本身的优先级数据,不能超过队列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(1);
            return message;
        });
}
```

发送第二条消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 2",message -> {
            //消息本身的优先级数据,不能超过队列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(2);
            return message;
        });
}
```

发送第三条消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 3",message -> {
            //消息本身的优先级数据,不能超过队列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(3);
            return message;
        });
}
```

**3、启动客户端服务，查看日志输出情况**

```java
public static final String QUEUE_PRIORITY = "queue.test.priority";

@RabbitListener(queues = {QUEUE_PRIORITY})
public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
    log.info("[priority]: " + dataString);
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

我们可以看到优先级高的先输出

![image-20241012165314324](【尚硅谷】RabbitMQ\670a38faf33bd.png)

# 集群搭建

## 安装RabbitMQ

### 前置要求

> 课程要求CentOS发行版的版本≥8，CentOS 7.x  其实也可以，后面有详细介绍

下载地址：https://mirrors.163.com/centos/

查看当前系统发行版本：

```bash
[root@localhost _data]# hostnamectl 
   Static hostname: localhost.localdomain
         Icon name: computer-vm
           Chassis: vm
        Machine ID: 1e9464680b694994bb37fa7013bd3ea7
           Boot ID: e0865df1adfa476eb633daed2637bff1
    Virtualization: vmware
  Operating System: CentOS Linux 7 (Core)
       CPE OS Name: cpe:/o:centos:centos:7
            Kernel: Linux 3.10.0-1160.90.1.el7.x86_64
      Architecture: x86-64
```



RabbitMQ安装方式官方指南：

> https://www.rabbitmq.com/docs/install-rpm

![image-20241012170624420](【尚硅谷】RabbitMQ\670a3c110e06a.png)

### 安装Erlang环境

#### **创建yum库配置文件**

```shell
vim /etc/yum.repos.d/rabbitmq.repo
```

#### **加入配置内容**

> 以下内容来自官网文档：https://www.rabbitmq.com/docs/install-rpm

```
# In /etc/yum.repos.d/rabbitmq.repo

##
## Zero dependency Erlang RPM
##

[modern-erlang]
name=modern-erlang-el9
# Use a set of mirrors maintained by the RabbitMQ core team.
# The mirrors have significantly higher bandwidth quotas.
baseurl=https://yum1.rabbitmq.com/erlang/el/9/$basearch
        https://yum2.rabbitmq.com/erlang/el/9/$basearch
repo_gpgcheck=1
enabled=1
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key
gpgcheck=1
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1
type=rpm-md

[modern-erlang-noarch]
name=modern-erlang-el9-noarch
# Use a set of mirrors maintained by the RabbitMQ core team.
# The mirrors have significantly higher bandwidth quotas.
baseurl=https://yum1.rabbitmq.com/erlang/el/9/noarch
        https://yum2.rabbitmq.com/erlang/el/9/noarch
repo_gpgcheck=1
enabled=1
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key
       https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc
gpgcheck=1
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1
type=rpm-md

[modern-erlang-source]
name=modern-erlang-el9-source
# Use a set of mirrors maintained by the RabbitMQ core team.
# The mirrors have significantly higher bandwidth quotas.
baseurl=https://yum1.rabbitmq.com/erlang/el/9/SRPMS
        https://yum2.rabbitmq.com/erlang/el/9/SRPMS
repo_gpgcheck=1
enabled=1
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key
       https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc
gpgcheck=1
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1


##
## RabbitMQ Server
##

[rabbitmq-el9]
name=rabbitmq-el9
baseurl=https://yum2.rabbitmq.com/rabbitmq/el/9/$basearch
        https://yum1.rabbitmq.com/rabbitmq/el/9/$basearch
repo_gpgcheck=1
enabled=1
# Cloudsmith's repository key and RabbitMQ package signing key
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key
       https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc
gpgcheck=1
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1
type=rpm-md

[rabbitmq-el9-noarch]
name=rabbitmq-el9-noarch
baseurl=https://yum2.rabbitmq.com/rabbitmq/el/9/noarch
        https://yum1.rabbitmq.com/rabbitmq/el/9/noarch
repo_gpgcheck=1
enabled=1
# Cloudsmith's repository key and RabbitMQ package signing key
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key
       https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc
gpgcheck=1
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1
type=rpm-md

[rabbitmq-el9-source]
name=rabbitmq-el9-source
baseurl=https://yum2.rabbitmq.com/rabbitmq/el/9/SRPMS
        https://yum1.rabbitmq.com/rabbitmq/el/9/SRPMS
repo_gpgcheck=1
enabled=1
gpgkey=https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key
gpgcheck=0
sslverify=1
sslcacert=/etc/pki/tls/certs/ca-bundle.crt
metadata_expire=300
pkg_gpgcheck=1
autorefresh=1
type=rpm-md
```

#### **更新yum库**

> --nobest 表示所需安装包即使不是最佳选择也接收

```shell
yum update -y --nobest
```

若不支持系统`--nobest`参数则可不使用

```shell
yum update -y 
```

![image-20241012171955556](【尚硅谷】RabbitMQ\670a3f3c5005e.png)

#### **正式安装Erlang**

##### CentOS 8


```shell
yum install -y erlang
```

##### **CentOS 7**

**卸载旧版本**

若未安装过，可跳过

> **卸载旧版本的 Erlang**
>
> 1. **查找已安装的 Erlang 包：**
>
>    ```bash
>    rpm -qa | grep erlang
>    ```
>
> 2. **卸载旧版本的 Erlang**：
>
>    ```bash
>    sudo yum remove erlang-26.2.5.4-1.el7.x86_64
>    ```
>
> **检查并删除残留文件**
>
> **确保系统中没有其他 Erlang 版本的残留文件或配置。**
>
> 1. **查找并删除所有与 Erlang 相关的目录**：
>
>    ```bash
>    sudo find / -name "erlang" -type d -exec rm -rf {} +
>    ```
>
> 2. **查找并删除所有与 Erlang 相关的文件**：
>
>    ```bash
>    sudo find / -name "*erlang*" -type f -exec rm -f {} +
>    ```
>
> 3. **查找并删除所有与 Erlang 相关的符号链接**：
>
>    ```bash
>    sudo find /usr/bin /usr/local/bin -name "erl*" -type l -exec rm -f {} +



安装时需要注意Erlang与CentOS的版本匹配，详细介绍见官网： https://www.rabbitmq.com/docs/which-erlang

![image-20241013120828545](【尚硅谷】RabbitMQ\670b47bc9a9df.png)

如课程中RabbitMQ使用的是`v3.13.0`，erlang需要安装的版本需要 >= 26.0

由于`rabbitmq-server` 安装包支持CentOS7的版本较老，如 `v3.9.16`，兼容的erlang最低版本为23.3，最高24.3

![image-20241013122208421](【尚硅谷】RabbitMQ\670b4af04aeb2.png)



**通过RPM安装**

> 可参考文章：[OpenCloudOS 8配置rabbitmq](https://blog.csdn.net/MeltryLL/article/details/141437375)

下载地址：https://github.com/rabbitmq/erlang-rpm/releases

我们需要下载与之相兼容的erlang版本如 [erlang-23.3-2.el7.x86_64.rpm](https://github.com/rabbitmq/erlang-rpm/releases/tag/v23.3)， el7 代表 CentOS 7

GitHub仓库地址： https://github.com/rabbitmq/erlang-rpm/releases

![image-20241013122538176](【尚硅谷】RabbitMQ\670b4bc1f1519.png)

将文件上传到CentOS的某个目录上，如`/opt/rabbitmq`

```bash
# 安装
sudo rpm -ivh erlang-23.3-2.el7.x86_64.rpm

# 检查 Erlang 版本，验证 Erlang 是否安装成功
# Erlang (SMP,ASYNC_THREADS,HIPE) (BEAM) emulator version 11.2
erl -version

# 或者用erl命令,其中OTP 23是我们安装的版本,erts-11.2是lib库依赖的版本
#Erlang/OTP 23 [erts-11.2] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1] [hipe]

#Eshell V11.2  (abort with ^G)
erl
```



**通过yum 安装**


> 可参考文章： [CentOS 7 安装Erlang、RabbitMQ（亲测通过）](https://blog.csdn.net/mumanquan1/article/details/122074059)

Erlang安装包下载地址： https://packagecloud.io/rabbitmq/erlang 

选择与`rabbitmq-server` 相兼容的版本，如 `erlang-23.3.4.11-1.el7.x86_64.rpm`，el7 代表适用CentOS7

> 若执行第一步出现如下错误
>
> ```bash
> [root@localhost ~]# curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash
> Detected operating system as centos/7.
> Checking for curl...
> Detected curl...
> Downloading repository file: https://packagecloud.io/install/repositories/rabbitmq/erlang/config_file.repo?os=centos&dist=7&source=script
> done.
> Attempting to install pygpgme for your os/dist: centos/7. Only required on older OSes to verify GPG signatures.
> Installing yum-utils...
>   File "/bin/yum", line 30
>     except KeyboardInterrupt, e:
>                             ^
> SyntaxError: invalid syntax
> Generating yum cache for rabbitmq_erlang...
>   File "/bin/yum", line 30
>     except KeyboardInterrupt, e:
>                             ^
> SyntaxError: invalid syntax
> Generating yum cache for rabbitmq_erlang-source...
>   File "/bin/yum", line 30
>     except KeyboardInterrupt, e:
>                             ^
> SyntaxError: invalid syntax
> 
> The repository is setup! You can now install packages.
> ```
>
> 检查Python版本
>
> ```bash
> [root@localhost ~]# python --version
> Python 3.7.0
> ```
>
> 若为3.x，执行如下命令创建软连接，使用python2执行
>
> ```
> sudo ln -sf /usr/bin/python2 /usr/bin/python
> ```

```shell
# 步骤 1：安装了存储库
curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash

# 步骤 2：安装软件包
sudo yum install -y erlang-23.3.4.11-1.el7.x86_64 
```

若下载失败可到官网手动下载安装

下载地址：https://www.erlang.org/downloads，会跳转至GitHub

GitHub: https://github.com/erlang/otp/releases

![image-20241013130235515](【尚硅谷】RabbitMQ\670b546b5e5c4.png)

下载完成后，将文件上传到某个目录，如`/opt/rabbitmq`，通过以下代码完成安装

```bash
# 使用 yum 包管理器安装 GCC 编译器，-y 选项表示自动回答 "yes" 以确认所有提示
yum -y install gcc

# 使用 tar 命令解压 Erlang 源码包，-z 选项表示使用 gzip 解压，-x 选项表示解压，-v 选项表示显示详细信息，-f 选项指定文件名
tar -zxvf otp_src_23.3.4.11.tar.gz

# 进入解压后的 Erlang 源码目录
cd /opt/rabbitmq/otp_src_23.3.4.11/

# 运行 configure 脚本，--prefix 选项指定 Erlang 的安装路径为 /usr/local/erlang
./configure --prefix=/usr/local/erlang

# 编译并安装 Erlang，make install 会执行编译后的安装步骤
make install
```

查看是否安装成功以及设置环境变量

```shell
# 列出 /usr/local/erlang/bin 目录下的所有文件和目录，ll 是 ls -l 的别名，显示详细信息
ll /usr/local/erlang/bin

# 将 Erlang 的 bin 目录添加到系统的 PATH 环境变量中，以便在终端中可以直接使用 Erlang 命令
echo 'export PATH=$PATH:/usr/local/erlang/bin' >> /etc/profile

# 重新加载 /etc/profile 文件，使环境变量配置立即生效
source /etc/profile

# 检查 Erlang 版本，验证 Erlang 是否安装成功
# Erlang (SMP,ASYNC_THREADS,HIPE) (BEAM) emulator version 11.2.2.10
erl -version

# 或者用 erl 验证
# Erlang/OTP 23 [erts-11.2.2.10] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1] #[hipe]

#Eshell V11.2.2.10  (abort with ^G)
#1>

erl
```



> 安装Erlang最新版会遇到的坑

此处发现打印的是版本是 `14.2.5.4`

```bash
erl -version
Erlang (SMP,ASYNC_THREADS) (BEAM) emulator version 14.2.5.4
```

使用 `erl`验证下，发现

```bash
[root@localhost rabbitmq]# erl
Erlang/OTP 26 [erts-14.2.5.4] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1]

Eshell V14.2.5.4 (press Ctrl+G to abort, type help(). for help)
1>
```

安装RabbitMQ时提示如下错误

```bash
[root@localhost rabbitmq]# rpm -ivh rabbitmq-server-3.13.0-1.el8.noarch.rpm 错误：依赖检测失败：        erlang >= 26.0 被 rabbitmq-server-3.13.0-1.el8.noarch 需要
```

### **安装RabbitMQ**

#### CentOS 8

```shell
# 导入GPG密钥
rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc'

rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key'

rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key'

# 下载 RPM 包
# 若下载失败多尝试几次或CentOS重启后重新尝试
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.13.0/rabbitmq-server-3.13.0-1.el8.noarch.rpm

# 安装
rpm -ivh rabbitmq-server-3.13.0-1.el8.noarch.rpm
```

#### CentOS 7
通过[Release Information | RabbitMQ](https://www.rabbitmq.com/release-information)跳转到github下载界面

https://github.com/rabbitmq/rabbitmq-server/releases

选择与`rabbitmq-server` 相兼容的版本，如  [rabbitmq-server-3.9.16-1.el7.noarch.rpm](https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.9.16)

![image-20241013002916641](【尚硅谷】RabbitMQ\670aa3dd08f44.png)

上传到CentOS某个目录，如 `/opt/rabbitmq`

```bash
# 安装
rpm -ivh rabbitmq-server-3.9.16-1.el7.noarch.rpm

# 显示如下信息代表安装成功
#警告：rabbitmq-server-3.9.16-1.el7.noarch.rpm: 头V4 RSA/SHA512 Signature, 密钥 ID 6026dfca: #NOKEY
#准备中...                          ################################# [100%]
#正在升级/安装...
#   1:rabbitmq-server-3.9.16-1.el7     ################################# [100%]
```

### **RabbitMQ基础配置**

> 启动服务前注意停用之前的Docker服务，以免造成端口冲突

```shell
# 启用管理界面插件
rabbitmq-plugins enable rabbitmq_management

# 启动 RabbitMQ 服务
systemctl start rabbitmq-server

# 将 RabbitMQ 服务设置为开机自动启动
systemctl enable rabbitmq-server

# 新增登录账号密码
rabbitmqctl add_user shiguang 123456

# 设置登录账号权限
rabbitmqctl set_user_tags shiguang administrator
rabbitmqctl set_permissions -p / shiguang ".*" ".*" ".*"

# 设置所有稳定功能 flag 启动
rabbitmqctl enable_feature_flag all

# 重新启动 RabbitMQ服务
systemctl restart rabbitmq-server
```

### 收尾工作

> 若不删除该配置，以后用yum安装会受到该配置影响

```bash
rm -rf /etc/yum.repos.d/rabbitmq.repo
```

## 克隆 VMWare虚拟机

### 目标

通过克隆操作，一共准备三台VMWare虚拟机

| 集群节点名称 | 虚拟机IP地址  |
| ------------ | ------------- |
| node01       | 192.168.10.66 |
| node02       | 192.168.10.88 |
| node03       | 192.168.10.99 |

### 克隆虚拟机

需克隆完整连接

![image-20241013132556306](【尚硅谷】RabbitMQ\670b59e4277f9.png)

需要

![image-20241013132634295](【尚硅谷】RabbitMQ\670b5a0a12674.png)

### 给新机器设置IP地址

在CentOS 7 中，可以使用`nmcli`命令行工具修改IP地址。以下是具体步骤：

1、查看网络连接信息：

```bash
nmcli con show 
```

2、停止指定的网络连接（将 <connection_name>替换为实际的网络连接名称）：

```bash
nmcli con down <connection_name>
```

3、修改IP地址（将 <connection_name>替换为实际的网络连接名称，将 <new_ip_address>替换为新的IP地址，将<subnet_mask>替换为子网掩码，将\<gateway>替换为网关）

```bash
# <new_ip_address>/<subnet_mask>这里是 CIDR 表示法
nmcli con mod <connection_name> ipv4.addresses <new_ip_address>/<subnet_mask>
nmcli con mod <connection_name> ipv4.gateway <gateway>
nmcli con mod <connection_name> ipv4.method manual # 手动
```

4、启动网络连接

```bash
nmcli con up <connection_name>
```

5、验证新的IP地址是否生效：

```bash
ip addr show
```

### 修改主机名称

主机名称会被RabbitMQ作为集群中的节点名称，后面会用到，所以需要设置一下。
修改后需重启

```bash
# 查看当前系统名称
cat /etc/hostname
# 修改当前系统名称
vim /etc/hostname
```

### 保险措施

为了在后续操作过程中，万一遇到操作失误，友情建议拍摄快照。

## 集群节点彼此发现

### node01设置

① 设置IP地址到主机名称的映射

修改文件`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下内容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 查看当前RabbitMQ节点的Cookie值并记录

```bash
cat /var/lib/rabbitmq/.erlang.cookie
```

显示如下：

```bash
[root@node01 ~]# cat /var/lib/rabbitmq/.erlang.cookie
KFGJAHXELTVBZJVTEHSG[root@node01 ~]#
```

③ 重置节点应用

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app
```

### node02设置

① 设置P地址到主机名称的映射

修改文件`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下内容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 修改当前RabbitMQ节点的Cookie值
node02和node03都改成和node01一样：

```bash
vim /var/lib/rabbitmq/.erlang.cookie
```

③ 重置节点应用并加入集群

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@node01
rabbitmqctl start_app
```

### node03设置

① 设置P地址到主机名称的映射

修改文件`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下内容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 修改当前RabbitMQ节点的Cookie值
node02和node03都改成和node01一样：

```bash
vim /var/lib/rabbitmq/.erlang.cookie
```

③ 重置节点应用并加入集群

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@node01
rabbitmqctl start_app
```

④ 查看集群状态

```bash
rabbitmqctl cluster_status
```

显示如下：

```bash
[root@node01 ~]# rabbitmqctl cluster_status
Cluster status of node rabbit@node01 ...
Basics

Cluster name: rabbit@node01

Disk Nodes

rabbit@node01
rabbit@node02
rabbit@node03

Running Nodes

rabbit@node01
rabbit@node02
rabbit@node03

Versions

rabbit@node01: RabbitMQ 3.9.16 on Erlang 23.3
rabbit@node02: RabbitMQ 3.9.16 on Erlang 23.3.4.11
rabbit@node03: RabbitMQ 3.9.16 on Erlang 23.3.4.11

Maintenance status

Node: rabbit@node01, status: not under maintenance
Node: rabbit@node02, status: not under maintenance
Node: rabbit@node03, status: not under maintenance

Alarms

(none)

Network Partitions

(none)

Listeners

Node: rabbit@node01, interface: [::], port: 25672, protocol: clustering, purpose: inter-node and CLI tool communication
Node: rabbit@node01, interface: [::], port: 15672, protocol: http, purpose: HTTP API
Node: rabbit@node01, interface: [::], port: 5672, protocol: amqp, purpose: AMQP 0-9-1 and AMQP 1.0
Node: rabbit@node02, interface: [::], port: 15672, protocol: http, purpose: HTTP API
Node: rabbit@node02, interface: [::], port: 25672, protocol: clustering, purpose: inter-node and CLI tool communication
Node: rabbit@node02, interface: [::], port: 5672, protocol: amqp, purpose: AMQP 0-9-1 and AMQP 1.0
Node: rabbit@node03, interface: [::], port: 15672, protocol: http, purpose: HTTP API
Node: rabbit@node03, interface: [::], port: 25672, protocol: clustering, purpose: inter-node and CLI tool communication
Node: rabbit@node03, interface: [::], port: 5672, protocol: amqp, purpose: AMQP 0-9-1 and AMQP 1.0

Feature flags

Flag: drop_unroutable_metric, state: enabled
Flag: empty_basic_get_metric, state: enabled
Flag: implicit_default_bindings, state: enabled
Flag: maintenance_mode_status, state: enabled
Flag: quorum_queue, state: enabled
Flag: stream_queue, state: enabled
Flag: user_limits, state: enabled
Flag: virtual_host_metadata, state: enabled
```

也可登录管理后台查看

![image-20241013150943009](【尚硅谷】RabbitMQ\670b7236e8402.png)



## 负载均衡：Management UI

### 说明

两个需要暴露的端口：

![image-20241013151148707](【尚硅谷】RabbitMQ\670b72b46ef57.png)

目前集群方案：

![image-20241013151250667](【尚硅谷】RabbitMQ\670b72f26a90b.png)

管理界面负载均衡：

![image-20241013151402696](【尚硅谷】RabbitMQ\670b733a7ea3f.png)

核心功能负载均衡：

![image-20241013151456881](【尚硅谷】RabbitMQ\670b7370b02e3.png)

### 安装HAProxy

```bash
# 安装
yum install -y haproxy

# 检查是否安装成功
haproxy -v

# 启动
systemctl start haproxy

# 设置开机自启动
systemctl enable haproxy
```

### 修改配置文件

> 配置文件位置：/etc/haproxy/haproxy.cfg

在配置文件未尾增加如下内容：

```bash
frontend rabbitmq_ui_frontend
bind 192.168.10.66:22222
mode http
default_backend rabbitmq_ui_backend

backend rabbitmq_ui_backend
mode http
balance roundrobin
option httpchk GET /
server rabbitmq_ui1 192.168.10.66:15672 check
server rabbitmq_ui2 192.168.10.88:15672 check
server rabbitmq_ui3 192.168.10.99:15672 check
```

设置SELinux策略，允许HAProxy拥有权限连接任意端口：

> SELinux是Linux系统中的安全模块，它可以限制进程的权限以提高系统的安全性。在某些情况下，SELinux可能会阻止HAProxy绑定指定的端口，这就需要通过设置域(domain)的安全策略来解决此问题。
>
> 通过执行setsebool-P haproxy_connect_any=1命令，您已经为HAProxyi设置了一个布尔值，允许HAProxy连接到任意端口。这样，HAProxy就可以成功绑定指定的socket,并正常工作。

```bash
setsebool -P haproxy_connect_any=1
```


重启HAProxy

```bssh
systemctl restart haproxy
```

### 测试效果

访问配置的前台负载均衡地址： http://192.168.10.66:22222

查看是否可以正常打开rabbitmq管理端界面

![image-20241013153928411](【尚硅谷】RabbitMQ\670b793069de5.png)

## 负载均衡：核心功能

### 添加配置

> 配置文件位置：/etc/haproxy/haproxy.cfg

在配置文件未尾增加如下内容：

```bash
frontend rabbitmq_frontend
bind 192.168.10.66:11111
mode tcp
default_backend rabbitmq_backend

backend rabbitmq_backend
mode tcp
balance roundrobin
server rabbitmq1 192.168.10.66:5672 check
server rabbitmq2 192.168.10.88:5672 check
server rabbitmq3 192.168.10.99:5672 check
```

重启HAProxy

```bssh
systemctl restart haproxy
```

### 测试

#### **创建组件**

- 交换机：exchange.cluster.test
- 队列；queue.cluster.test
- 路由键：routing.key.cluster.test

#### **创建生产者端程序**

**1、POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.shiguang</groupId>
    <artifactId>module08-cluster-producer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```

**2、核心配置文件**

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 11111
    username: shiguang
    password: 123456
    virtual-host: /
    publisher-confirm-type: CORRELATED #交换机的确认
    publisher-returns: true #队列的确认
logging:
  level:
    com.shiguang.mq.config.MQProducerAckConfig: info
```

**3、配置类**

```java
package com.shiguang.mq.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created By Shiguang On 2024/10/13 16:15
 */

@Configuration
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 消息发送到交换机成功或失败时调用这个方法
     *
     * @param correlationData 用于关联消息的唯一标识符
     * @param ack             表示消息是否被成功确认
     * @param cause           如果消息确认失败，这里会包含失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送到交换机成功！数据: " + correlationData);
        } else {
            log.info("消息发送到交换机失败！ 数据: " + correlationData + " 错误原因: " + cause);
        }

    }

    /**
     * 当消息无法路由到队列时调用这个方法
     *
     * @param returnedMessage 包含无法路由的消息的详细信息
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage() 回调函数 消息主体: " + new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数 应答码: " + returnedMessage.getReplyCode());
        log.info("returnedMessage() 回调函数 描述: " + returnedMessage.getReplyText());
        log.info("returnedMessage() 回调函数 消息使用的交换器 exchange: " + returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数 消息使用的路由键 routing: " + returnedMessage.getRoutingKey());
    }
}
```

**4、启动类**

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQProducerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQProducerMainType.class, args);
    }
}
```

**5、测试类**

```java
package com.shiguang.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Created By Shiguang On 2024/10/11 20:16
 */

@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_CLUSTER_TEST = "exchange.cluster.test";
    public static final String ROUTING_KEY_CLUSTER_TEST = "routing.key.cluster.test";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSendMessage() {

        String message = "Test Send Message By Cluster !!";

        rabbitTemplate.convertAndSend(EXCHANGE_CLUSTER_TEST, ROUTING_KEY_CLUSTER_TEST, message);
    }


}
```



#### 创建消费者端程序

**1、POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.chiguang</groupId>
    <artifactId>module09-cluster-consumer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
    </parent>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- springboot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- rabbitmq -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>

</project>
```

**2、核心配置文件**

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 11111
    username: shiguang
    password: 123456
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual #手动确认
logging:
  level:
    com.shiguang.mq.config.MQProducerAckConfig: info
```

**3、Listener**

```java
package com.shiguang.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String QUEUE_CLUSTER = "queue.cluster.test";

    @RabbitListener(queues = {QUEUE_CLUSTER})
    public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
        log.info("[消费者端] 消息内容: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
```

**4、启动类**

```java
package com.shiguang.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created By Shiguang On 2024/10/11 16:03
 */
@SpringBootApplication
public class RabbitMQConsumerMainType {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQConsumerMainType.class, args);
    }
}
```

#### 运行结果

![image-20241013164328717](【尚硅谷】RabbitMQ\670b883067356.png)

## 镜像队列

> 镜像队列在新版本中已被仲裁队列取代，这里不再介绍

## 仲裁队列

RabbitMQ3.8.x版本的主要更新内容，未来有可能取代Classic Queue

创建仲裁队列，可以将队列同步到集群中的每个节点上

![image-20241013164832214](【尚硅谷】RabbitMQ\670b895fe4c73.png)

### 操作步骤

#### 创建仲裁队列

> 需要在集群的基础上创建

**1、创建交换机**

和仲裁队列绑定的交换机没有特殊要求，我们还是创建一个direct交换机即可
交换机名称：exchange.quorum.test

**2、创建仲裁队列**

队列名称：queue.quorum.test

![image-20241013165350867](【尚硅谷】RabbitMQ\670b8a9e7fddc.png)

创建好后如图所示：

![image-20241013165518171](【尚硅谷】RabbitMQ\670b8af5cbc51.png)

详情信息：

![image-20241013165545104](【尚硅谷】RabbitMQ\670b8b10c7469.png)



3、绑定交换机

路由键：routing.key.quorum.test

#### 测试

##### 常规测试

像使用经典队列一样发送消息、消费消息

**① 生产者端**

```java
public static final String EXCHANGE_QUORUM_TEST = "exchange.quorum.test";
public static final String ROUTING_KEY_QUORUM_TEST = "routing.key.quorum.test";

@Test
public void testSendMessageToQuorum() {

    String message = "Test Send Message By Quorum!!";

    rabbitTemplate.convertAndSend(EXCHANGE_QUORUM_TEST, ROUTING_KEY_QUORUM_TEST, message);
}

```

日志输出情况：

![image-20241013170259621](【尚硅谷】RabbitMQ\670b8cc33938f.png)

队列情况：

![image-20241013170225207](【尚硅谷】RabbitMQ\670b8ca0d734a.png)



**② 消费者端**

```java
public static final String QUEUE_QUORUM_TEST = "queue.quorum.test";

@RabbitListener(queues = {QUEUE_QUORUM_TEST})
public void processQuorumMessage(String dataString, Message message, Channel channel) throws IOException {
    log.info("[消费者端] 消息内容: " + dataString);
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

日志输出情况：

![image-20241013170711764](【尚硅谷】RabbitMQ\670b8dbf5bb45.png)

队列情况：

![image-20241013170736519](【尚硅谷】RabbitMQ\670b8dd82bbb1.png)



##### 高可用测试

① 停止某个节点的rabbit应用

```bash
# 停止rabbit应用
rabbitmqctl stop_app
```

此时可以再观察下队列详情，可以发现已自动选举出新的Leader

![image-20241013171039544](【尚硅谷】RabbitMQ\670b8e8f41fb3.png)



② 再次发送消息

修改发送消息的内容，以区分之前发送的消息，消费者端能够正常消费

控制台有报错是因为有节点下线，属于正常情况

![image-20241013171344976](【尚硅谷】RabbitMQ\670b8f48b9a4b.png)



## 流式队列

RabbitMQ在 3.9.x 推出的新特性

**工作机制**：

在一个仅追加的日志文件内保存所发送的消息

![image-20241013171615604](【尚硅谷】RabbitMQ\670b8fdf3b1e5.png)

给每个消息都分配个偏移页，即使消息被消费端消费掉，消息依然保存在日志文件中，可重复消费



![image-20241013171638560](【尚硅谷】RabbitMQ\670b8ff637f5a.png)

**总体评价**

- 从客户端支持角度来说，生态尚不健全
- 从使用习惯角度来说，和原有队列用法不完全兼容
- 从竞品角度来说，**像Kafka，但远远比不上Kafka**
- 从应用场景角度来说：
  - 经典队列：适用于系统内部异步通信场景
  - 流式队列：适用于系统间跨平台、大流量、实时计算场景(Kafka主场)
- 使用建议：Stream队列在目前企业实际应用非常少，真有特定场景需要使用肯定会倾向于使用Kafka,而不是RabbitMQ Stream
- 未来展望：Classic Queue已经有和Quorum Queue合二为一的趋势,Stream也有加入进来整合成一种队列的趋势，但Stream内部机制决定这很难

### 使用步骤

#### 启用插件

> 说明：只有启用了Stream插件，才能使用流式队列的完整功能

在集群每个节点中依次执行如下操作：

```bash
# 启用Stream插件
rabbitmq-plugins enable rabbitmq_stream 

# 重启rabbit应用
rabbitmqctl stop_app
rabbitmqctl start_app

# 查看插件状态
rabbitmq-plugins list
```

![image-20241013201150462](【尚硅谷】RabbitMQ\670bb9061efb6.png)

#### 负载均衡

> 配置文件位置：/etc/haproxy/haproxy.cfg

在配置文件未尾增加如下内容：

```bash
frontend rabbitmq_stream_frontend
bind 192.168.10.66:33333
mode tcp
default_backend rabbitmq_stream_backend

backend rabbitmq_stream_backend
mode tcp
balance roundrobin
server rabbitmq1 192.168.10.66:5552 check
server rabbitmq2 192.168.10.88:5552 check
server rabbitmq3 192.168.10.99:5552 check
```

重启HAProxy

```bssh
systemctl restart haproxy
```

#### JAVA代码

Stream专属Java客户端官方网址：https://github.com/rabbitmq/rabbitmq-stream-java-client
Stream专属Java客户端官方文档网址：https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/htmlsingle/

##### 引入依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.rabbitmq</groupId>
        <artifactId>stream-client</artifactId>
        <version>0.15.0</version>
    </dependency>

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.30</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.5.8</version>
    </dependency>
</dependencies>
```

##### 创建Stream

> 不需要创建交换机

**① 代码方式创建**

```java
Environment environment = Environment.builder()
    .host("192.168.10.66")
    .port(33333)
    .username("shiguang")
    .password("123456")
    .build();

environment.streamCreator().stream("stream.shiguang.test").create();
```



**② ManagementUlt创建**

![image-20241013202817496](【尚硅谷】RabbitMQ\670bbce107546.png)

##### 生产端程序

**① 内部机制说明**
[1] 官方文档

> Internally，the Environment will query the broker to find out about the topology of the stream and will create or re-use a connection to publish to the leader node of the stream.

翻译：

> 在内部，Environment将查问brokerl以了解流的拓扑结构，并将创建或重用连接以发布到流的leader节点。

[2] 解析

- 在Environment中封装的连接信息仅负责连接到 broker
- Producer在构建对象时会访问broker拉取集群中 Leader 的连接信息
- 将来实际访问的是集群中的 Leader 节点
- Leader的连接信息格式是：节点名称:端口号

![image-20241013203356647](【尚硅谷】RabbitMQ\670bbe344e090.png)

[3] 配置

> 文件位置： C:\Windows\System32\drivers\etc

为了让本机的应用程序知道Leader节点名称对应的IP地址，我们需要在**本地**配置hosts文件，建立从节点名称到P地址的映射关系

```tex
# rabbitmq 测试
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```



**② 示例代码**

```java
Environment environment = Environment.builder()
    .host("192.168.10.66")
    .port(33333)
    .username("shiguang")
    .password("123456")
    .build();

Producer producer = environment.producerBuilder()
    .stream("stream.shiguang.test")
    .build();

byte[] messagePayload = "hello rabbit stream".getBytes(StandardCharsets.UTF_8);

CountDownLatch countDownLatch = new CountDownLatch(1);

producer.send(
    producer.messageBuilder().addData(messagePayload).build(),
    confirmationStatus -> {
        if (confirmationStatus.isConfirmed()) {
            System.out.println("[生产者端]the message made it to the broker");
        } else {
            System.out.println("[生产者端]the message did not make it to the broker");
        }
        countDownLatch.countDown();
    });
countDownLatch.await();
producer.close();
environment.close();
```

##### 消费端程序

```java
Environment environment = Environment.builder()
    .host("192.168.10.66")
    .port(33333)
    .username("shiguang")
    .password("123456")
    .build();

environment.consumerBuilder()
    .stream("stream.shiguang.test")
    .name("stream.shiguang.test.consumer")
    .autoTrackingStrategy()
    .builder()
    .messageHandler((offset, message) -> {
        byte[] bodyAsBinary = message.getBodyAsBinary();
        String messageContent = new String(bodyAsBinary);
        System.out.println("[消费者] messagecontent = " + messageContent + " offset = " + offset.offset());
    })
    .build();
```

#### 指定偏移量消费

##### 偏移量

![image-20241013211641886](【尚硅谷】RabbitMQ\670bc8395c066.png)

##### 官网文档说明

![image-20241013211716855](【尚硅谷】RabbitMQ\670bc85c7e1d5.png)

##### 指定Offset消费

```java
Environment environment = Environment.builder()
    .host("192.168.10.66")
    .port(33333)
    .username("shiguang")
    .password("123456")
    .build();

CountDownLatch countDownLatch = new CountDownLatch(1);

Consumer consumer = environment.consumerBuilder()
    .stream("stream.shiguang.test")
    .offset(OffsetSpecification.first())
    .messageHandler((offset, message) -> {
        byte[] bodyAsBinary = message.getBodyAsBinary();
        String messageContent = new String(bodyAsBinary);
        System.out.println("[消费者端] messagecontent = " + messageContent);
        countDownLatch.countDown();
    })
    .build();

countDownLatch.await();
consumer.close();
```

##### 对比

- autoTrackingStrategy方式：始终监听Stream中的新消息（狗狗看家，忠于职守）
- 指定偏移量方式：针对指定偏移量的消息消费之后就停止（狗狗叼飞盘，叼回来就完）

# Federation插件

## 简介

Federation插件的设计目标是使RabbitMQ在不同的Broker节点之间进行消息传送而无须建立集群。

它可以在不同的管理域中的Broker或集群间传递消息，这些管理域可能设置了不同的用户和vhost,也可能运行在不同版本的RabbitMQ和Erang上，Federation基于AMOP 0-9-1协议在不同的Broker之间进行通信。并且设计成能够密忍不稳定的网络连接情况。

## Federation交换机

### 总体说明

- 各节点操作：启用联邦插件
- 下游操作：
  - 添加上游连接端点
  - 创建控制策略

### 准备工作

为了执行相关测试，我们使用Dockert创建两个RabbitMQ实例。
**特别提示**：由于Federation机制的最大特点就是跨集群同步数据，所以这两个Docker容器中的RabbitMQ实例不加入集群！！！是两个**独立的broker实例**。

```bash
# 上游
docker run -d \
--name rabbitmq-shenzhen \
-p 51000:5672 \
-p 52000:15672 \
-v rabbitmq-plugin:/plugins \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=123456 \
rabbitmq:3.13-management

# 下游
docker run -d \
--name rabbitmq-shanghai \
-p 61000:5672 \
-p 62000:15672 \
-v rabbitmq-plugin:/plugins \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=123456 \
rabbitmq:3.13-management
```

### 启用联邦插件

在上游、下游节点中都需要开启。
Docker容器中的RabbitMQ已经开启了rabbitmq_federation,还需要开启rabbitmq_federation_management

```bash
# 使用以下命令进入 RabbitMQ 容器的 shell
docker exec -it <container_name> /bin/bash

rabbitmq-plugins enable rabbitmq_federation

rabbitmq-plugins enable rabbitmq_federation_management
```

rabbitmq_federation_management插件启用后会在Management Ul的Admin选项卡下看到：

![image-20241013222423034](【尚硅谷】RabbitMQ\670bd81692b37.png)

### 添加上游连接端点

在下游节点填写上游节点的连接信息：

```bash
# Name
shiguang.upstream
# URI
amqp://guest:[redacted]@192.168.10.66:51000
```



![image-20241013222715218](【尚硅谷】RabbitMQ\670bd8c2cc5b2.png)

### 创建控制策略

![image-20241013222955450](【尚硅谷】RabbitMQ\670bd963385ee.png)



![image-20241013222923676](【尚硅谷】RabbitMQ\670bd9433b9e8.png)

详细配置如下：

```bash
# Name
police.federation.exchange

# Pattern
^federated\.

# Apply to
Exchanges

# Priority
10

# Definition
federation-upstream = shiguang.upstream
```

![image-20241013224442084](【尚硅谷】RabbitMQ\670bdcd98f54c.png)

### 测试

**① 测试计划**
**特别提示**：

- 普通交换机和联邦交换机名称要一致
- 交换机名称要能够和策略正则表达式匹配上
- 发送消息时，两边使用的路由键也要一致
- 队列名称不要求一致

![image-20241013223528412](【尚硅谷】RabbitMQ\670bdaafd9145.png)



**② 创建组件**

| 所在机房         | 交换机名称              | 路由键                | 队列名称              |
| ---------------- | ----------------------- | --------------------- | --------------------- |
| 深圳机房（上游） | federated.exchange.demo | routing.key.demo.test | queue.normal.shenzhen |
| 上海机房（下游） | federated.exchange.demo | routing.key.demo.test | queue.normal.shanghai |

创建组件后可以查看一下联邦状态，连接成功的联邦状态如下：

![image-20241013224525828](【尚硅谷】RabbitMQ\670bdd054c145.png)

③ 发布消息执行测试

在上游节点向交换机发布消息：

![image-20241013224710446](【尚硅谷】RabbitMQ\670bdd6df06ef.png)

下游两个队列消息总量均变成了1

![image-20241013224807815](【尚硅谷】RabbitMQ\670bdda746017.png)



## Federation队列

### 总体说明

Federation队列和Federation交换机的最核心区别就是：

- Federation Police作用在交换机上，就是Federation交换机
- Federation Police作用在队列上，就是Federation队列

### 创建控制策略

![image-20241013224953436](【尚硅谷】RabbitMQ\670bde11003d4.png)

详细配置如下：

```bash
# Name
police.federation.queue

# Pattern
^fed\.queue\.

# Apply to
Queues

# Priority
10

# Definition
federation-upstream = shiguang.upstream
```

![image-20241013225354586](【尚硅谷】RabbitMQ\670bdf0212196.png)

### 测试

**① 测试计划**
上游节点和下游节点中队列名称是相同的，只是下游队列中的节点附加了联邦策略而已

| 所在机房         | 交换机名称               | 路由键                      | 队列名称       |
| ---------------- | ------------------------ | --------------------------- | -------------- |
| 深圳机房（上游） | exchange.normal.shenzhen | routing.key.normal.shenzhen | fed.queue.demo |
| 上海机房（下游） | ——                       | ——                          | fed.queue.demo |

**② 创建组件**
上游节点都是常规操作，此处省略。重点需要关注的是下游节点的联邦队列创建时需要指定相关参数：
创建组件后可以查看一下联邦状态，连接成功的联邦状态如下：

![image-20241013231417003](【尚硅谷】RabbitMQ\670be3c863697.png)

**③ 执行测试**
在上游节点向交换机发布消息：
![image-20241013231549413](【尚硅谷】RabbitMQ\670be424d1e9e.png)

但此时发现下游节点中联邦队列并没有接收到消息

![image-20241013231659313](【尚硅谷】RabbitMQ\670be46aa74b7.png)

这是为什么呢？这里就体现出了联邦队列和联邦交换机工作逻辑的区别。
对联邦队列来说，如果没有监响联队列的消费端程序，它是不会到上游去拉取消息的！
如果有消费端监听联邦队列，那么首先消费联邦队列自身的消息；**如果联邦队列为空，这时候才会到上游队列节点中拉取消息。**
所以现在的测试效果需要消费端程序配合才能看到：

![image-20241013232845847](【尚硅谷】RabbitMQ\670be72d370db.png)

# Shovel插件

> Shovel 是铲子的意思，把消息铲走，从源节点移至目标节点，源节点将收不到消息

## 启用Shovel插件

```bash
# 使用以下命令进入 RabbitMQ 容器的 shell
docker exec -it <container_name> /bin/bash

rabbitmq-plugins enable rabbitmq_shovel

rabbitmq-plugins enable rabbitmq_shovel_management
```

启用后管理界面可以看到如下配置：

![image-20241013233431738](【尚硅谷】RabbitMQ\670be88711637.png)

## 配置Shovel

> 不区分上下游，在哪个节点配置都可以

```bash
# Name
shiguang.shovel.config

# Source URI shenzhen
amqp://guest:123456@192.168.10.66:51000

# Source Queue
queue.shovel.demo.shenzhen

# Destination URI shanghai 
amqp://guest:123456@192.168.10.66:61000

# Destination Queue
queue.shovel.demo.shanghai
```

![image-20241014001059119](【尚硅谷】RabbitMQ\670bf11299cce.png)

## 测试

### **测试计划**

| 所在机房 | 交换机名称           | 路由键               | 队列名称                   |
| -------- | -------------------- | -------------------- | -------------------------- |
| 深圳机房 | exchange.shovel.test | exchange.shovel.test | queue.shovel.demo.shenzhen |
| 上海机房 | ——                   | ——                   | queue.shovel.demo.shanghai |

### 测试效果

**① 发布消息**

![image-20241013234805964](【尚硅谷】RabbitMQ\670bebb5718d6.png)



**② 源节点**

![image-20241014003755567](【尚硅谷】RabbitMQ\670bf762e144f.png)



**③ 目标节点**


![image-20241014003806164](【尚硅谷】RabbitMQ\670bf76d75248.png)


如果测试效果与视频中演示不一致，可检查配置的账号密码是否正确
可用 `docker logs  <container_name/container_id> 查看日志`

![image-20241014105658504](【尚硅谷】RabbitMQ\670c887a10928.png)

可点击 Shovels Name 查看配置详情，例如此处我错误地将用户名写为`gust`，正确应为 `guest`

![image-20241014110145653](【尚硅谷】RabbitMQ\670c8998e97e0.png)

如果账号密码配置错误导致无法连接，实际测试效果将和普通队列相同

源节点：
![image-20241013235205538](【尚硅谷】RabbitMQ\670beca4dd4ad.png)

目标节点：
![image-20241013235231335](【尚硅谷】RabbitMQ\670becbea5201.png)
