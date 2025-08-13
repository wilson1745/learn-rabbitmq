> 線上視頻:[尚矽谷2024最新RabbitMQ教程，消息中介軟體RabbitMQ迅速上手！](https://www.bilibili.com/video/BV1sw4m1U7Qe)
> 官方資料: [尚矽谷2024最新版RabbitMQ視頻](https://pan.baidu.com/s/14quDrJSfphJfC6seNo6-CA?pwd=yyds  )
>
> 代碼
> Gitee：https://gitee.com/an_shiguang/learn-rabbitmq
> GitHub: https://github.com/Shiguang-coding/learn-rabbitmq

# MQ的相關概念

## 什麼是MQ

MQ(message queue),從字面意思上看，本質是個佇列，FIFO先入先出，只不過佇列中存放的內容是message而已，還是一種跨進程的通信機制，用於上下游傳遞消息。在互聯網架構中，MQ是一種非常常見的上下游“邏輯解耦+物理解耦”的消息通信服務。使用了MQ之後，消息發送上游只需要依賴MQ,不用依賴其他服務。

## 為什麼要用MQ

### 流量消峰

舉個例子，如果訂單系統最多能處理一萬次訂單，這個處理能力應付正常時段的下單時綽綽有餘，正常時段我們下單一秒後就能返回結果。但是在高峰期，如果有兩萬次下單作業系統是處理不了的，只能限制訂單超過一萬後不允許用戶下單。使用訊息佇列做緩衝，我們可以取消這個限制，把一秒內下的訂單分散成一段時間來處理，這時有些用戶可能在下單十幾秒後才能收到下單成功的操作，但是比不能下單的體驗要好。

![image-20241010204828111](assets/6707cd1d08c34.png)

### 應用解耦

以電商應用為例，應用中有訂單系統、庫存系統、物流系統、支付系統。使用者創建訂單後，如果耦合調用庫存系統、物流系統、支付系統，任何一個子系統出了故障，都會造成下單操作異常。當轉變成基於訊息佇列的方式後，系統間調用的問題會減少很多，比如物流系統因為發生故障，需要幾分鐘來修復。在這幾分鐘的時間裡，物流系統要處理的記憶體被緩存在訊息佇列中，用戶的下單操作可以正常完成。當物流系統恢復後，繼續處理訂單資訊即可，中單使用者感受不到物流系統的故障，提升系統的可用性。

![image-20241010205039359](assets/6707cd9f9d363.png)

### 非同步處理

有些服務間調用是非同步的，例如A調用B,B需要花費很長時間執行，但是A需要知道B什麼時候可以執行完，以前一般有兩種方式，A過一段時間去調用B的查詢api查詢。或者A提供一個callback api,B執行完之後調用api通知A服務。這兩種方式都不是很優雅，使用消息匯流排，可以很方便解決這個問題，A調用B服務後，只需要監聽B處理完成的消息，當B處理完成後，會發送一條消息給MQ,MQ會將此消息轉發給A服務。這樣A服務既不用迴圈調用B的查詢api,也不用提供callback api。同樣B服務也不用做這些操作。A服務還能及時的得到非同步處理成功的消息。

![image-20241010205241250](assets/6707ce1983cc7.png)

## MQ的分類

### 訊息佇列底層實現的兩大主流方式

- 由於訊息佇列執行的是跨應用的資訊傳遞，所以制定**底層通信標準**非常必要
- 目前主流的訊息佇列通信協議標準包括：
  - AMQP(Advanced Message Queuing Protocol):**通用**協議，IBM公司研發
  - JMS(Java Message Service):**專門**為**Java**語言服務，SUN公司研發，一組由Java介面組成的Java標準

### AMQP與JMS對比

![image-20241010205705458](assets/6707cf2221479.png)

### 各主流MQ產品對比

![image-20241010210033971](assets/6707cff25501d.png)



**1、ActiveMQ**

> [尚矽谷ActiveMQ教程(MQ消息中介軟體快速入門)](https://www.bilibili.com/video/BV164411G7aB)

優點：單機輸送量萬級，時效性ms級，可用性高，基於主從架構實現高可用性，消息可靠性較低的概率丟失資料
缺點：官方社區現在對ActiveMQ5.x維護越來越少，高輸送量場景較少使用。

**2、Kafka**

> [尚矽谷Kafka教程，2024新版kafka視頻，零基礎入門到實戰](https://www.bilibili.com/video/BV1Gp421m7UN)
>
> [【尚矽谷】Kafka3.x教程（從入門到調優，深入全面）](https://www.bilibili.com/video/BV1vr4y1677k)

大資料的殺手鐧，談到大資料領域內的消息傳輸，則繞不開Kafka,這款**為大資料而生**的消息中介軟體，以其**百萬級TPS**的輸送量名聲大噪，迅速成為大資料領域的寵兒，在資料獲取、傳輸、存諸的過程中發揮著舉足輕重的作用。目前已經被LinkedIn，Uber，Twitter，Netflix等大公司所採納。

優點：性能卓越，單機寫入TPS約在百萬條/秒，最大的優點，就是**輸送量高**。時效性ms級可用性非常高，kafka是分散式的，一個資料多個副本，少數機器宕機，不會丟失資料，不會導致不可用，消費者採用Pull方式獲取消息，消息有序，通過控制能夠保證所有消息被消費且僅被消費一次;有優秀的協力廠商Kafka Web管理介面Kafka-Manager;在日誌領域比較成熟，被多家公司和多個開源專案使用；功能支援：功能較為簡單，主要支援簡單的MQ功能，在大資料領域的即時計算以及**日誌採集**被大規模使用

缺點：Kafka單機超過64個佇列/分區，Load會發生明顯的飆高現象，佇列越多，load越高，發送消息回應時間變長，使用短輪詢方式，即時性取決於輪詢間隔時間，消費失敗不支持重試；支援消息順序，但是一台代理宕機後，就會產生消息亂序，**社區更新較慢**；

**3、RocketMQ**

> [【尚矽谷】RocketMQ教程丨深度掌握MQ消息中介軟體](https://www.bilibili.com/video/BV1cf4y157sz)

RocketMQ出自阿裡巴巴的開源產品，用java語言實現，在設計時參考了Kafka,並做出了自己的一些改進。被阿裡巴巴廣泛應用在訂單，交易，充值，流計算，消息推送，日誌流式處理，binglog分發等場景。

優點：單**機輸送量十萬級**，可用性非常高，分散式架構，**消息可以做到0丟失**，MQ功能較為完善，還是分散式的，擴展性好，**支援10億級別的消息堆積**，不會因為堆積導致性能下降，源碼是jva我們可以自己閱讀源碼，定制自己公司的MQ

缺點：**支援的用戶端語言不多**，目前是java及c++,其中c++不成熟；社區活躍度一般，沒有在MQ核心中去實現JMS等介面，有些系統要遷移需要修改大量代碼

**4、RabbitMQ**

2007年發佈，是一個在AMQP(高級訊息佇列協議)基礎上完成的，可複用的企業消息系統，是**當前最主流的消息中介軟體之一**。

優點：由於erlang語言的**高併發特性**，性能較好；**輸送量到萬級**，MQ功能比較完備，健壯、穩定、易用、跨平臺、**支援多種語言**如：Python、Ruby、.NET、Java、JS、C、PHP、ActionScript、XMPP、STOMP等，支持A]AX文檔齊全；開源提供的管理介面非常棒，用起來很好用，**社區活躍度高**；更新頻率相當高

缺點：商業版需要收費，學習成本較高

## MQ的選擇

**1、Kafka**
Kafka主要特點是基於Pul的模式來處理消息消費，追求高輸送量，一開始的目的就是用於日誌收集和傳輸，適合產生**大量資料**的互聯網服務的資料收集業務。**大型公司**建議可以選用，如果有**日誌採集**功能，肯定是首選kafka了。

**2、RocketMQ**
天生為**金融互聯網**領域而生，對於可靠性要求很高的場景，尤其是電商裡面的訂單扣款，以及業務削峰，在大量交易湧入時，後端可能無法及時處理的情況。RoketMQ在穩定性上可能更值得信賴，這些業務場景在阿裡雙11已經經歷了多次考驗，如果你的業務有上述併發場景，建議可以選擇RocketMQ。

**3、RabbitMQ**
結合erlang語言本身的併發優勢，性能好**時效性微秒級**，**社區活躍度也比較高**，管理介面用起來十分
方便，如果你的**資料量沒有那麼大**，中小型公司優先選擇功能比較完備的RabbitMQ。

# RabbitMQ介紹

## RabbitMQ的概念

RabbitMQ是一個消息中介軟體：它接受並轉發消息。你可以把它當做一個快遞網站，當你要發送一個包裹時，你把你的包裹放到快遞站，快遞員最終會把你的快遞送到收件人那裡，按照這種邏輯RabbitMQ是一個快遞站，一個快遞員幫你傳遞快件。RabbitMQ與快遞站的主要區別在於，它不處理快件而是接收，存儲和轉發消息資料。

## 四大核心概念

**生產者**
產生資料發送消息的程式是生產者

**交換機**
交換機是RabbitMQ非常重要的一個部件，一方面它接收來自生產者的消息，另一方面它將消息推送到佇列中。交換機必須確切知道如何處理它接收到的消息，是將這些消息推送到特定佇列還是推送到多個佇列，亦或者是把消息丟棄，這個得有切換類型決定。

**佇列**

佇列是RabbitMQ內部使用的一種資料結構，儘管消息流經RabbitMQ和應用程式，但它們只能存儲在佇列中。佇列僅受主機的記憶體和滋盤限制的約束，本質上是一個大的訊息緩衝區。許多生產者可以將消息發送到一個佇列，許多消費者可以嘗試從一個佇列接收資料。這就是我們使用佇列的方式。

**消費者**
消費與接收具有相似的含義。消費者大多時候是一個等待接收消息的程式。請注意生產者，消費者和消息中介軟體很多時候並不在同一機器上。同一個應用程式既可以是生產者又是可以是消費者。

![image-20241010213015369](assets/6707d6e7a8ae4.png)

## RabbitMQ核心部分

![image-20241010213451006](assets/6707d7fb6d265.png)

## 各個名詞介紹

![image-20241010213523739](assets/6707d81c1fa11.png)

**Broker**：接收和分發消息的應用， RabbitMQ Server 就是 Message Broker

**Virtual host**：出於多租戶和安全因素設計的，把 AMQP 的基本元件劃分到一個虛擬的分組中，類似於網路中的 namespace 概念。當多個不同的用戶使用同一個 RabbitMQ server 提供的服務時，可以劃分出多個 vhost，每個用戶在自己的 vhost 創建 exchange／ queue 等

**Connection**： publisher／ consumer 和 broker 之間的 TCP 連接

**Channel**：如果每一次訪問 RabbitMQ 都建立一個 Connection，在消息量大的時候建立 TCP Connection 的開銷將是巨大的，效率也較低。 Channel 是在 connection 內部建立的邏輯連接，如果應用程式支援多執行緒，通常每個 thread 創建單獨的 channel 進行通訊， AMQP method 包含了 channel id 幫助用戶端和 message broker 識別 channel，所以 channel 之間是完全隔離的。 Channel 作為羽量級的**Connection 極大減少了作業系統建立 TCP connection 的開銷**

**Exchange**： message 到達 broker 的第一站，根據分發規則，匹配查詢表中的 routing key，分發消息到 queue 中去。常用的類型有： direct (point-to-point), topic (publish-subscribe) and fanout (multicast)

**Queue**： 消息最終被送到這裡等待 consumer 取走

**Binding**： exchange 和 queue 之間的虛擬連接， binding 中可以包含 routing key， Binding 資訊被保存到 exchange 中的查詢表中，用於 message 的分發依據

# 安裝RabbitMQ

## 手動安裝

**1、官網地址**

https://www.rabbitmq.com/download.html

2、**檔上傳上傳到`/usr/local/software` 目錄下**(如果沒有 software 需要自己創建)

![image-20241010214418997](assets/6707da33347fe.png)

**3、安裝檔(分別按照以下順序安裝)**  

```bash
rpm -ivh erlang-21.3-1.el7.x86_64.rpm
yum install socat -y
rpm -ivh rabbitmq-server-3.8.8-1.el7.noarch.rpm
```

**4、常用命令(按照以下循序執行)**

添加開機啟動 RabbitMQ 服務

```bash
chkconfig rabbitmq-server on
```

啟動服務

```bash
/sbin/service rabbitmq-server start
```

查看服務狀態

```bash
/sbin/service rabbitmq-server status
```

![image-20241010214909944](assets/6707db57417c8.png)

停止服務(選擇執行)

```bash
/sbin/service rabbitmq-server stop
```

開啟 web 管理外掛程式

```bash
rabbitmq-plugins enable rabbitmq_management
```

用預設帳號密碼(guest)訪問位址 `http://47.115.185.244:15672`出現許可權問題  

![image-20241010214756433](assets/6707db0cbee0a.png)

**5、添加一個新的用戶**

創建帳號

```bash
rabbitmqctl add_user admin 123
```

設置用戶角色

```bash
rabbitmqctl set_user_tags admin administrator
```

設置用戶許可權

```bash
# set_permissions [-p <vhostpath>] <user> <conf> <write> <read> 
rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
```

用戶 user_admin 具有/vhost1 這個 virtual host 中所有資源的配置、寫、讀許可權

當前用戶和角色

```bash
rabbitmqctl list_users
```

**6、再次利用 admin 用戶登錄**  

![image-20241010215234896](assets/6707dc2323d67.png)

**7、重置命令**

關閉應用的命令為:

```bash
rabbitmqctl stop_app
```

清除的命令為

```bash
rabbitmqctl reset
```

重新啟動命令為

```bash
rabbitmqctl start_app
```

## Docker安裝

**1、安裝**

```bash
# 拉取鏡像
docker pull rabbitmq:3.13-management

# -d 參數：後臺運行 Docker 容器
# --name 參數：設置容器名稱
# -p 參數：映射埠號,格式為 "宿主機埠號:容器內部埠號" 5672供用戶端程式訪問,15672供後臺應用管理介面訪問
# -v 參數：卷映射目錄
# -e 參數：設置容器內的環境變數,這裡我們設置了登錄RabbitMQ管理後臺的預設使用者和密碼
docker run -d \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
-v rabbitmq-plugin:/plugins \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=123456 \
rabbitmq:3.13-management
```

**2、驗證**

訪問後臺管理介面： `http://<ip>:15672`

![image-20241010224411426](assets/6707e83b952cd.png)

登錄後介面如圖:
![image-20241010224526884](assets/6707e88738459.png)

# Hello World

我們將用java編寫兩個程式。發送單個消息的生產者和接收消息並列印出來的消費者。
下圖中，”P”是我們的生產者，”C”是我們的消費者。中間的框是一個佇列-RabbitMQ 代表使用者保留的訊息緩衝區

![image-20241011095319454](assets/670885105d9c1.png)

## 導入依賴

```pom
<dependency>
   <groupId>com.rabbitmq</groupId>
   <artifactId>amqp-client</artifactId>
   <version>5.20.0</version>
</dependency>
```

## 消息發送端(生產者)

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
        // 創建連接工廠
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 設置主機位址
        connectionFactory.setHost("192.168.10.66");

        // 設置埠號: 默認為5672
        connectionFactory.setPort(5672);

        // 虛擬主機名稱稱: 默認為/
        connectionFactory.setVirtualHost("/");

        // 設置連接用戶名: 默認為guest
        connectionFactory.setUsername("guest");

        // 設置連接密碼: 默認為guest
        connectionFactory.setPassword("123456");

        // 創建連接
        Connection connection = connectionFactory.newConnection();

        // 創建通道
        Channel channel = connection.createChannel();

        // 聲明佇列
        // queue 名稱
        // durable 是否持久化
        // exclusive 是否獨佔本次連接,若為true,則佇列僅在本次連接可見,連接關閉後,佇列自動刪除
        // autoDelete 是否自動刪除,若為true,則當最後一個消費者斷開連接後,佇列會被刪除
        // arguments 其他參數
        channel.queueDeclare("simple_queue", false, false, false, null);

        // 發佈消息
        String message = "hello rabbitmq";

        // exchange 交換機名稱
        // routingKey 路由鍵,用於將消息路由到指定的佇列,如果沒有指定,消息將發送到預設的交換機,默認的交換機名稱為空字串
        // props 消息屬性,用於設置消息的屬性,如消息的優先順序、過期時間等
        // body 消息體,即要發送的消息內容
        channel.basicPublish("", "simple_queue", null, message.getBytes());

        System.out.println("消息已發送:" + message + "");

        // 關閉資源
        channel.close();
        connection.close();

    }
}
```

執行後如下所示：

![image-20241011115518386](assets/6708a1a724d9e.png)

可以在後臺管理介面查看狀態

![image-20241011115406548](assets/6708a15f7c5d7.png)

查看訊息佇列

![image-20241011115428733](assets/6708a1759f72f.png)

## 消息接收端(消費者)

```JAVA
package com.shiguang.rabbitmq.simple;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 11:17
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1、創建一個ConnectionFactory，並設置主機名稱、埠號、虛擬主機、用戶名和密碼。
        ConnectionFactory factory = new ConnectionFactory();
        // 2、設置連接參數。
        factory.setHost("192.168.10.66");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("123456");

        // 3、通過工廠創建連接。
        Connection connection = factory.newConnection();

        // 4、通過連接創建通道。
        Channel channel = connection.createChannel();

        // 5、創建佇列，並指定佇列名稱、是否持久化、是否獨佔、是否自動刪除、其他參數。
        // 生產者已經創建了佇列，這裡不需要再創建
//        channel.queueDeclare("simple.queue", true, false, false, null);

        // 6、消費消息
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            // consumerTag 消費者標籤，用來標識消費者，在監聽消息時使用。
            // envelope 消息的中繼資料，包括交換機、路由鍵、投遞模式等。
            // properties 消息的屬性，如消息的優先順序、過期時間等。
            // body 消息的正文，即要發送的資料。
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

        // 7、註冊消費者，指定佇列名稱、是否自動回應、消費者。
        channel.basicConsume("simple_queue", true, consumer);

        // 8、關閉資源
        channel.close();
        connection.close();

    }
}
```

執行結果如下：

![image-20241011115754203](assets/6708a2430c500.png)

再次查看狀態

![image-20241011115834136](assets/6708a26b0a9ca.png)

再次查看訊息佇列

![image-20241011115900482](assets/6708a2856c188.png)

# RabbitMQ工作模式

## 工作模式概述

RabbitMQ有7種用法：

![image-20241011120726844](assets/6708a47fbeda7.png)

以下是 RabbitMQ 的一些常見用法：

1. 訊息佇列：

   RabbitMQ 最基本的用法是作為訊息佇列。生產者將消息發送到 RabbitMQ 伺服器，消費者從佇列中獲取消息並進行處理。這種模式可以實現應用程式的解耦和非同步通信。

2. 發佈/訂閱模式：

   RabbitMQ 支援發佈/訂閱模式，允許生產者將消息發佈到一個或多個交換機（Exchange），消費者訂閱感興趣的佇列。當有新消息到達時，RabbitMQ 會將消息路由到所有訂閱了相應佇列的消費者。

3. 路由模式：

   在路由模式中，生產者將消息發送到交換機，並指定一個路由鍵（Routing Key）。RabbitMQ 根據路由鍵將消息路由到綁定了相應路由鍵的佇列。這種模式可以實現更精細的消息路由。

4. 主題模式：

   主題模式是路由模式的擴展，允許使用萬用字元來匹配路由鍵。例如，可以使用“*”萬用字元匹配一個單詞，使用“#”萬用字元匹配任意數量的單詞。這種模式可以實現更靈活的消息路由。

5. RPC（遠程程序呼叫）：

   RabbitMQ 可以用於實現 RPC 機制，允許用戶端調用遠端伺服器上的方法。用戶端將請求消息發送到 RabbitMQ，伺服器處理請求並將回應訊息發送回用戶端。

## Work Queues

工作隊列(又稱任務佇列)的主要思想是避免立即執行資源密集型任務，而不得不等待它完成。相反我們安排任務在之後執行。我們把任務封裝為消息並將其發送到佇列。在後臺運行的工作進程將彈出任務並最終執行作業。 當有多個工作執行緒時，這些工作執行緒將一起處理這些任務。 

 

本質上我們剛剛寫的HelloWorld程式就是這種模式，只是簡化到了最簡單的情況：

- 生產者只有一個
- 發送一個消息
- 消費者也只有一個，消息也只能被這個消費者消費

所以HelloWorld也稱為簡單模式。



現在我們還原一下常規情況：

- 生產者發送多個消息
- 由多個消費者來競爭
- 誰搶到算誰的

結論：
多個消費者監聽同一個佇列，則各消費者之間對同一個消息是競爭的關係。
Work Queues工作模式適用於任務較重或任務較多的情況，多消費者分攤任務，可以提高消息處理的效率

### 生產者代碼

#### 封裝工具類

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
     * 獲取與 RabbitMQ 伺服器的連接
     *
     * @return 與 RabbitMQ 伺服器的連線物件
     * @throws Exception 如果在創建連接時發生錯誤
     */
    public static Connection getConnection() throws Exception {
        // 創建一個新的連接工廠物件
        ConnectionFactory factory = new ConnectionFactory();
        // 設置 RabbitMQ 伺服器的主機位址
        factory.setHost(HOST_ADDRESS);
        // 設置 RabbitMQ 伺服器的埠號
        factory.setPort(PORT);
        // 設置 RabbitMQ 伺服器的虛擬主機
        factory.setVirtualHost(VIRTUAL_HOST);
        // 設置連接 RabbitMQ 伺服器的用戶名
        factory.setUsername(USERNAME);
        // 設置連接 RabbitMQ 伺服器的密碼
        factory.setPassword(PASSWORD);
        // 返回新創建的連線物件
        return factory.newConnection();
    }

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("連接成功!!");
            System.out.println("connection = " + connection + "");
        } else {
            System.out.println("連接失敗!!");
        }

    }


}
```

#### 編寫代碼

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

#### 發送消息效果

![image-20241011124518118](assets/6708ad5f0d0d5.png)

### 消費者代碼

#### 編寫代碼

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

#### 運行效果

Consumer1：

![image-20241011125514415](assets/6708afb32f01c.png)

Consumer2:

![image-20241011125554724](assets/6708afdb74210.png)

## 發佈訂閱模式（Publish/Subscribe）

Publish/Subscribe模式需要引入新角色：交換機

- 生產者不是把消息直接發送到佇列，而是發送到交換機
- 交換機接收消息，而如何處理消息取決於交換機的類型
- 交換機有如下3種常見類型
  - Fanout: 廣播，將消息發送給所有綁定到交換機的佇列
  - Direct: 定向，把消息交給符合指定routing key的佇列
  - Topic: 萬用字元，把消息交給符合routing pattern(路由模式)的佇列

注意：Exchange(交換機)**只負責轉發**消息，**不具備存儲**消息的能力，因此如果沒有任何佇列與Exchange綁定，或者沒有符合路由規側的佇列，那麼消息會丟失！



元件之間關係：

- 生產者把消息發送到交換機
- 佇列直接和交換機綁定

工作機制：消息發送到交換機上，就會以**廣播**的形式發送給所有已綁定佇列

理解概念：

- Publish:發佈，這裡就是把消息發送到交換機上
- Subscribe:訂閱，這裡只要把佇列和交換機綁定，事實上就形成了一種訂閱關係

### 生產者代碼

#### 編寫代碼

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
        // 1、獲取連接
        Connection connection = ConnectionUtil.getConnection();

        // 2、獲取通道
        Channel channel = connection.createChannel();

        // 3、聲明交換機
        // exchange 交換機名稱
        // type 切換類型
        // durable 是否持久化
        // autoDelete 是否自動刪除
        // arguments 其他參數
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, false,null);

        // 4、創建佇列
        channel.queueDeclare("fanout_queue1", true, false, false, null);
        channel.queueDeclare("fanout_queue2", true, false, false, null);

        // 5、綁定佇列到交換機
        // queue 佇列名稱
        // exchange 交換機名稱
        // routingKey 路由鍵, 用於指定消息的路由規則
        channel.queueBind("fanout_queue1", EXCHANGE_NAME, "");
        channel.queueBind("fanout_queue2", EXCHANGE_NAME, "");

        // 6、發送消息
        String body = "日誌資訊: 張三調用了findAll方法 ";
        channel.basicPublish(EXCHANGE_NAME, "", null, body.getBytes());

        // 7、釋放資源
        channel.close();
        connection.close();

    }
}
```



#### 執行效果

可以通過後臺查看我們剛創建的交換機

![image-20241011132250767](assets/6708b62baa05d.png)

點擊 `Name` 欄的交換機名稱跳轉到詳情頁，展開`Bindings`查看該交換機綁定的訊息佇列

![image-20241011132455222](assets/6708b6a8161c8.png)

可以看到新增兩個訊息佇列並分別發送了一條消息

![image-20241011132707238](assets/6708b72c07708.png)

點擊`Name`欄的訊息佇列名稱可查看詳情

![image-20241011132939726](assets/6708b7c48f285.png)

通過`Get Messages(s)`按鈕可以查看消息詳情

![image-20241011133113587](assets/6708b8224c1e8.png)

### 消費者代碼

#### 編寫代碼

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
                System.out.println("佇列1 消費者1 日誌列印...");
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
                System.out.println("佇列2 消費者2 日誌列印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```

#### 執行效果

> 示例代碼兩個Consumer分別綁定不同的訊息佇列，為非競爭關係，若綁定相同的訊息佇列則為競爭關係

Consumer1：

![image-20241011134111290](assets/6708ba78076b5.png)

Consumer2：

![image-20241011134139332](assets/6708ba940f5bd.png)

## 路由模式（Routing）

- 通過 **路由綁定 **的方式，把交換機和佇列關聯起來
- 交換機和佇列通過路由鍵進行綁定
- 生產者發送消息時不僅要指定交換機，還要指定路由鍵
- 交換機接收到消息會發送到路由鍵綁定的佇列
- 在編碼上與Publish/Subscribe發佈與訂閱模式的區別：
  - 交換機的類型為：Direct
  - 佇列綁定交換機的時候需要指定routing key。

![image-20241011134835789](assets/6708bc347bd0f.png)



### 生產者代碼

#### 編寫代碼

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
        // 1、獲取連接
        Connection connection = ConnectionUtil.getConnection();

        // 2、獲取通道
        Channel channel = connection.createChannel();

        // 3、聲明交換機
        // exchange 交換機名稱
        // type 切換類型
        // durable 是否持久化
        // autoDelete 是否自動刪除
        // arguments 其他參數
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false,null);

        String queue1Name = "direct_queue1";
        String queue2Name = "direct_queue2";

        // 4、創建佇列
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);

        // 5、綁定佇列到交換機
        // queue 佇列名稱
        // exchange 交換機名稱
        // routingKey 路由鍵, 用於指定消息的路由規則
        // 佇列1 綁定 error 路由鍵
        channel.queueBind(queue1Name, EXCHANGE_NAME, "error");
        // 佇列2 綁定info、error、warning 路由鍵
        channel.queueBind(queue2Name, EXCHANGE_NAME, "info");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "error");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "warning");

        // 6、發送消息
        String body = "日誌資訊: 張三調用了delete方法. 執行出錯,日誌級別warning";
        channel.basicPublish(EXCHANGE_NAME, "warning", null, body.getBytes());

        System.out.println("body發送成功: " + body );

        // 7、釋放資源
        channel.close();
        connection.close();

    }
}
```

#### 運行效果

新創建的交換機如圖所示

![image-20241011140558047](assets/6708c046f1a9e.png)

詳情如圖所示，可以看到綁定了兩個訊息佇列`direct_queue1` 和`direct_queue2`，`direct_queue1`關聯`error`一個路由鍵，`direct_queue2`關聯了`error`、`info`、`warning`三個路由鍵

![image-20241011140653263](assets/6708c07e170b5.png)

### 消費者代碼

#### 編寫代碼

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
                System.out.println("佇列1 消費者1 日誌列印...");
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
                System.out.println("佇列2 消費者2 日誌列印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```

#### 執行效果

由於我們只往`warning`路由鍵發送消息，而 `direct_queue1`關聯`error`一個路由鍵，`direct_queue2`關聯了`error`、`info`、`warning`三個路由鍵，所以`Consumer1`收不到消息， `Consumer2`可以收到消息

Consumer1：

![image-20241011141728610](assets/6708c2f953c68.png)

Consumer2：

![image-20241011141757516](assets/6708c31643ee0.png)

我們可以修改為往`error`路由鍵發送消息，這樣兩個消費者就都能接收到消息了

```java
String body = "日誌資訊: 張三調用了delete方法. 執行出錯,日誌級別error";
channel.basicPublish(EXCHANGE_NAME, "error", null, body.getBytes());
```

Consumer1：

![image-20241011142312968](assets/6708c451a5e53.png)

Consumer2：

![image-20241011142342393](assets/6708c46f0b97a.png)

## 主題模式（Topics）

- Topic類型與Direct相比，都是可以根據RoutingKey把消息路由到不同的佇列。只不過Topic類型Exchange可以讓佇列在綁定Routing key的時候使用萬用字元
- Routingkey一般都是由一個或多個單詞組成，多個單詞之間以"`.`"分割，例如：`item.insert`
- 萬用字元規則：
  - #: 匹配零個或多個詞
  - *: 匹配一個詞、

![image-20241011142915903](assets/6708c5bc94a7b.png)



假設有一個主題交換機 `logs`，並且有以下佇列和綁定：

- 佇列 `critical_errors` 綁定鍵為 `*.error`
- 佇列 `user_logs` 綁定鍵為 `user.*`
- 佇列 `all_logs` 綁定鍵為 `#`

如果生產者發送一條路由鍵為 `user.info` 的消息，那麼這條消息將被路由到 `user_logs` 和 `all_logs` 佇列。

如果生產者發送一條路由鍵為 `system.error` 的消息，那麼這條消息將被路由到 `critical_errors` 和 `all_logs` 佇列。

![image-20241011152112193](assets/6708d1e8e698f.png)

### 生產者代碼

#### 編寫代碼

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
        // 1、獲取連接
        Connection connection = ConnectionUtil.getConnection();

        // 2、獲取通道
        Channel channel = connection.createChannel();

        // 3、聲明交換機
        // exchange 交換機名稱
        // type 切換類型
        // durable 是否持久化
        // autoDelete 是否自動刪除
        // arguments 其他參數
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, false,null);

        String queue1Name = "topic_queue1";
        String queue2Name = "topic_queue2";

        // 4、創建佇列
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);

        // 5、綁定佇列到交換機
        // queue 佇列名稱
        // exchange 交換機名稱
        // routingKey 路由鍵, 用於指定消息的路由規則
        // routingKey常用格式: 系統名稱.日誌級別
        // 需求: 所有error級別日誌存入資料庫,所有order系統的日誌存入資料庫
        channel.queueBind(queue1Name, EXCHANGE_NAME, "#.error");
        channel.queueBind(queue1Name, EXCHANGE_NAME, "order.*");
        channel.queueBind(queue2Name, EXCHANGE_NAME, "*.*");
//        channel.queueBind(queue2Name, EXCHANGE_NAME, "#");

        // 6、發送消息
        // 分別發送消息到佇列: order.info、goods.info、goods.error
        String body = "[所在系統：order][日誌級別：info][日誌內容: 訂單生成,保存成功]";
        channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
        System.out.println("body發送成功: " + body );

//        body = "[所在系統：goods][日誌級別：info][日誌內容: 商品發佈成功]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
//        System.out.println("body發送成功: " + body );
//
//        body = "[所在系統：goods][日誌級別：error][日誌內容: 商品發佈失敗]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body發送成功: " + body );

        // 7、釋放資源
        channel.close();
        connection.close();

    }
}
```



#### 執行效果

創建的交換機資訊如圖所示

![image-20241011145223731](assets/6708cb287c4af.png)

創建的訊息佇列如圖所示：

![image-20241011145131503](assets/6708caf44daca.png)

### 消費者代碼

#### 編寫代碼

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
                System.out.println("佇列1 消費者1 日誌列印...");
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
                System.out.println("佇列2 消費者2 日誌列印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
```



#### 執行效果

`topic_queue1`匹配規則滿足：所有error級別日誌存入資料庫,所有order系統的日誌存入資料庫

`topic_queue2`則匹配所有消息

```java
channel.queueBind(queue1Name, EXCHANGE_NAME, "#.error");
channel.queueBind(queue1Name, EXCHANGE_NAME, "order.*");
channel.queueBind(queue2Name, EXCHANGE_NAME, "*.*");
```

我們先發送`order.info`規則的消息，執行並查看效果

```java
// 6、發送消息
// 分別發送消息到佇列: order.info、goods.info、goods.error
String body = "[所在系統：order][日誌級別：info][日誌內容: 訂單生成,保存成功]";
channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
System.out.println("body發送成功: " + body );

//        body = "[所在系統：goods][日誌級別：info][日誌內容: 商品發佈成功]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
//        System.out.println("body發送成功: " + body );
//
//        body = "[所在系統：goods][日誌級別：error][日誌內容: 商品發佈失敗]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body發送成功: " + body );
```

由於`topic_queue1`與`topic_queue2`均能匹配`order.info`規則，所以`Consumer1`與`Consumer2`均能接收到消息。

Consumer1：

![image-20241011145503121](assets/6708cbc7c3e9b.png)

Consumer2：

![image-20241011145532452](assets/6708cbe52a6da.png)

我們再發送`goods.info`這個規則的消息，清空Consumer日誌，重新發送消息

```java
// 6、發送消息
// 分別發送消息到佇列: order.info、goods.info、goods.error
String body = "[所在系統：order][日誌級別：info][日誌內容: 訂單生成,保存成功]";
channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
System.out.println("body發送成功: " + body );

body = "[所在系統：goods][日誌級別：info][日誌內容: 商品發佈成功]";
channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
System.out.println("body發送成功: " + body );
//
//        body = "[所在系統：goods][日誌級別：error][日誌內容: 商品發佈失敗]";
//        channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
//        System.out.println("body發送成功: " + body );
```

由於`topic_queue1`不能匹配`goods.info`規則，所以`Consumer1`只接收到一條消息，`Consumer2`接收到兩條消息。

Consumer1：

![image-20241011150647279](assets/6708ce87df55f.png)

Consumer2：

![image-20241011150733592](assets/6708ceb642782.png)

我們繼續追加`goods.error`這個規則的消息

```java
// 6、發送消息
// 分別發送消息到佇列: order.info、goods.info、goods.error
String body = "[所在系統：order][日誌級別：info][日誌內容: 訂單生成,保存成功]";
channel.basicPublish(EXCHANGE_NAME, "order.info", null, body.getBytes());
System.out.println("body發送成功: " + body );

body = "[所在系統：goods][日誌級別：info][日誌內容: 商品發佈成功]";
channel.basicPublish(EXCHANGE_NAME, "goods.info", null, body.getBytes());
System.out.println("body發送成功: " + body );

body = "[所在系統：goods][日誌級別：error][日誌內容: 商品發佈失敗]";
channel.basicPublish(EXCHANGE_NAME, "goods.error", null, body.getBytes());
System.out.println("body發送成功: " + body );
```

同理可知`Consumer1`只接收到兩條消息，`Consumer2`接收到三條消息。

Consumer1：

![image-20241011151629375](assets/6708d0ce0861b.png)

Consumer2：

![image-20241011151643198](assets/6708d0dbd51f1.png)

## 遠程程序呼叫（RPC）

- 遠端程序呼叫，本質上是同步調用，和我們使用OpenFeign調用遠端介面一樣
- 所以這不是典型的訊息佇列工作方式，我們不展開說明

![image-20241011152301703](assets/6708d2565e857.png)



## 工作模式小結

直接發送到佇列：底層使用了預設交換機

經過交換機發送到佇列

- Fanout: 沒有Routing key直接綁定佇列
- Direct: 通過Routing key綁定佇列，消息發送到綁定的佇列上
  - 一個交換機綁定一個佇列：定點發送
  - 一個交換機綁定多個佇列：廣播發送
- Topic: 針對Routing key使用萬用字元



# Spring Boot 整合RabbitMQ

## 基本思路

- 搭建環境
- 基礎設定：交換機名稱、佇列名稱、綁定關係
- 發送消息：使用`RabbitTemplate`
- 接收消息：使用`@RabbitListener`注解

## 消費者操作步驟

### 創建項目並導入依賴

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

### 創建設定檔

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

### 創建啟動類

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

    // 寫法一: 監聽 + 在 RabbitMQ 伺服器上創建交換機、佇列、綁定關係
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = QUEUE_NAME, durable = "true"),
//            exchange = @Exchange(value = EXCHANGE_DIRECT),
//            key = {ROUTING_KEY}
//    ))
//    public void processMessage(String dataString, Message message, Channel channel) {
//        log.info("消費端接收到消息：{}", dataString);
//        System.out.println("消費端接收到消息：" + dataString);
//    }

    // 寫法二: 只監聽
    @RabbitListener(queues = QUEUE_NAME)
    public void processMessage(String dataString, Message message, Channel channel) {
        log.info("消費端接收到消息：{}", dataString);
        System.out.println("消費端接收到消息：" + dataString);
    }
}
```

### 測試

啟動服務，登錄RabbitMQ管理介面查看交換機，訊息佇列是否創建成功並建立綁定關係

**交換機：**

![image-20241011163324060](assets/6708e2d4c1531.png)

**訊息佇列：**

![image-20241011163402237](assets/6708e2fad2e01.png)

**綁定關係：**

![image-20241011163446266](assets/6708e326ea4f9.png)

### 圖形化介面操作

**創建交換機：**

![image-20241011163749986](assets/6708e3deac9fd.png)

**創建訊息佇列：**

![image-20241011163909201](assets/6708e42df31c6.png)

**建立綁定關係：**

![image-20241011164049337](assets/6708e49205b0a.png)

添加後如下：

![image-20241011164111141](assets/6708e4a7aeefd.png)

## 生產者操作步驟

### 創建項目並導入依賴

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

### 創建設定檔

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
```

創建啟動類

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

創建測試類

> 注意測試類包路徑應與專案啟動類所屬包路徑一致，否則@Autowired無法自動裝配

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

### 測試

執行測試代碼，查看後臺監控，有一條消息待消費

![image-20241011191042896](assets/670907b38d80e.png)

啟動消費者服務進行消費

![image-20241011191209907](assets/6709080a8d4ab.png)

# 消息可靠性投遞

## 問題場景及解決方案

### 問題場景

下單操作的正常流程如下圖所示

![image-20241011191734902](assets/6709094f6ed58.png)

故障情況1：消息沒有發送到訊息佇列上
後果：消費者拿不到消息，業務功能缺失，資料錯誤

![image-20241011191841070](assets/6709099187a59.png)

故障情況2：消息成功存入訊息佇列，但是訊息佇列伺服器宕機了
原本保存在**<font color = 'red'>記憶體中的消息</font>**也**<font color = 'red'>丟失</font>**了
即使伺服器重新開機，消息也找不回來了
後果：消費者拿不到消息，業務功能缺失，資料錯誤

![image-20241011191929644](assets/670909c226d6e.png)

故障情況3：消息成功存入訊息佇列，但是消費端出現問題，例如：宕機、拋異常等等

後果：業務功能缺失，資料錯誤

![image-20241011192236717](assets/67090a7d3dbb9.png)

### 解決方案

故障情況1：消息沒有發送到訊息佇列

- 解決思路A：在**生產者端**進行確認，具體操作中我們會分別針對**交換機**和**佇列**來確認
  如果沒有成功發送到訊息佇列伺服器上，那就可以嘗試重新發送
- 解決思路B：為目標交換機指定**備份交換機**，當目標交換機投遞失敗時，把消息投遞至
  備份交換機

故障情況2：訊息佇列伺服器宕機導致記憶體中消息丟失

- 解決思路：**消息持久化**到硬碟上，哪怕伺服器重啟也不會導致消息丟失

故障情況3：消費端宕機或拋異常導致消息沒有成功被消費

- 消費端消費消息**成功**，給伺服器返回**ACK資訊**，然後訊息佇列刪除該消息
- 消費端消費消息**失敗**，給伺服器端返回**NACK資訊**，同時把消息恢復為**待消費**的狀態，
  這樣就可以再次取回消息，**重試**一次（當然，這就需要消費端介面支援冪等性）

## 故障情況1

### 生產者端實現

#### 創建項目並導入依賴

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



#### 主啟動類

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

#### 設定檔

> 注意：publisher-confirm-type和publisher-returns是兩個必須要增加的配置，如果沒有則本節功能不生效

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 5672
    username: guest
    password: 123456
    virtual-host: /
    publisher-confirm-type: CORRELATED #交換機的確認
    publisher-returns: true #佇列的確認
logging:
  level:
    com.shiguang.mq.config.MQProducerAckConfig: info
```

#### 配置類

**目標**：首先我們需要聲明回呼函數來接收RabbitMQ伺服器返回的確認資訊：

| 方法名            | 方法功能                 | 所屬介面        | 介面所屬類     |
| ----------------- | ------------------------ | --------------- | -------------- |
| confirm()         | 確認消息是否發送到交換機 | ConfirmCallback | RabbitTemplate |
| returnedMessage() | 確認消息是否發送到佇列   | ReturnsCallback | RabbitTemplate |



然後，就是對RabbitTemplate的功能進行增強，因為回呼函數所在物件必須設置到RabbitTemplate物件中才能生效
原本RabbitTemplate物件並沒有生產者端消息確認的功能，要給它設置對應的元件才可以。
而設置對應的元件，需要調用RabbitTemplate物件下面兩個方法：

| 設置元件調用的方法   | 所需物件類型            |
| -------------------- | ----------------------- |
| setConfirmCallback() | ConfirmCallback介面類別型 |
| setReturnCallback()  | ReturnCallback:介面類別型 |

代碼如下：

> ① 要點1
> 加@Component注解，加入IOC容器（@Configuration已經包含了@Component）
> ② 要點2
> 配置類自身實現ConfirmCallback、ReturnCallbacki這兩個介面，然後通過this指標把配置類的物件設置到RabbitTemplate物件中。
> 操作封裝到了一個專門的void init()方法中。
> 為了保證這個void init()方法在應用啟動時被調用，我們使用@PostConstruct注解來修飾這個方法。
> 關於@PostConstruct注解大家可以參照以下說明：
>
> @PostConstruct注解是**java中的一個標準注解**，它用於指定在**物件創建之後立即執行**的方法。當使用依賴注入（如Spring框架）或者其他方式創建物件時，@PostConstruct注解可以確保在物件完全初始化之後，執行相應的方法。
>
> 使用@PostConstructi注解的方法必須滿足以下條件：
>
> 1. 方法不能有任何參數
> 2. 方法必須是非靜態的
> 3. 方法不能返回任何值。
>
> 當容器產生實體一個帶有@PostConstruct注解的Bean時，它會在**調用構造函數之後**，並在**依賴注入完成之前**調用被@PostConstruct注解標記的方法。這樣，我們可以在該方法中進行一些初始化操作，比如讀取設定檔、建立資料庫連接等。

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
     * 消息發送到交換機成功或失敗時調用這個方法
     *
     * @param correlationData 用於關聯消息的唯一識別碼
     * @param ack             表示消息是否被成功確認
     * @param cause           如果消息確認失敗，這裡會包含失敗的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("confirm() 回呼函數列印 CorrelationData: " + correlationData);
        log.info("confirm() 回呼函數列印 ack: " + ack);
        log.info("confirm() 回呼函數列印 cause: " + cause);
    }

    /**
     * 當消息無法路由到佇列時調用這個方法
     *
     * @param returnedMessage 包含無法路由的消息的詳細資訊
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage() 回呼函數 消息主體: " + new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回呼函數 應答碼: " + returnedMessage.getReplyCode());
        log.info("returnedMessage() 回呼函數 描述: " + returnedMessage.getReplyText());
        log.info("returnedMessage() 回呼函數 消息使用的交換器 exchange: " + returnedMessage.getExchange());
        log.info("returnedMessage() 回呼函數 消息使用的路由鍵 routing: " + returnedMessage.getRoutingKey());
    }
}
```

#### 測試類

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

#### 測試

正常執行測試代碼，查看日誌輸出，ack為`true`，cause為`null`

![image-20241011202138328](assets/67091852e853f.png)

調整交換機名稱，故意使其發送失敗

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
}
```

重新執行並查看日誌輸出，ack為`false`，cause有相應錯誤原因

![image-20241011202706175](assets/6709199aa83f7.png)

調整路由鍵名稱，故意使其無法匹配

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY + "~", message);
}
```

重新執行並查看日誌輸出，列印了`returnedMessage()`回到函數日誌

![image-20241011203140202](assets/67091aacbb997.png)

### 備份交換機實現

![image-20241011203624907](assets/67091bc963bbf.png)

**1、創建備份交換機**

類型必須為`fanout`，因為消息從目標交換機轉至備份交換機時是沒有路由鍵的，只能通過廣播的方式查找佇列。

![image-20241011210002157](assets/6709215299a4b.png)

**2、創建佇列**

![image-20241011210325574](assets/6709221e137cb.png)

**3、交換機綁定佇列**

![image-20241011210448331](assets/67092270c6687.png)

**4、執行目標交換機的備份交換機**

由於交換機創建後參數無法修改，所以需要將原來的目標刪除重新創建並執行備份交換機

刪除原來的目標交換機：

![image-20241011210908870](assets/6709237572ecc.png)

重新創建目標交換機：

![image-20241011211146489](assets/670924133a9ef.png)

佇列重新綁定交換機：

![image-20241011211619274](assets/67092523b71bb.png)

**5、重新執行測試**

```java
@Test
public void test01SendMessage() {
    String message = "Message Confirm Test !!";
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    //        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
    rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY + "~", message);
}
```

測試結果：ack為`true`

![image-20241011211911885](assets/670925d05eceb.png)

`queue.test.backup`有一條消息待消費
![image-20241011212032489](assets/67092620e9231.png)




## 故障情況2

預設情況下，RabbitMQ服務宕機後，消息會丟失嗎?

我們手動重啟下RabbitMQ服務，然後查看消息消費情況

```bash
docker restart rabbitmq
```

原來有一條消息待消費

![image-20241011212807995](assets/670927e8711d7.png)

重啟後重新查看，發現帶消費消息從0條轉變為1條，我們並未重新發送消息，但消息並未丟失

![image-20241011213202836](assets/670928d33cb1e.png)

其實預設情況下，RabbitMQ是支援持久化資料的，重啟後會將保存到磁片的資料重新載入到記憶體中

我們可以查看下`@RabbitListener` 注解的源碼，找到`Queue`這個介面

```java
Queue[] queuesToDeclare() default {};
```

可以看到，`durable()`和 `autoDelete()`雖然預設值都為空，但源碼注釋中有說明，默認是支持持久化但是並不會自動刪除的。

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

## 故障情況3

### 消費者端實現

#### 創建項目並導入依賴

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

#### 主啟動類

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

#### 設定檔

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
        acknowledge-mode: manual # 把消息確認模式改為手動確認
logging:
  level:
    com.shiguang.mq.listener.MyMessageListener: info
```

#### Listener

> channel.basicNack與channel.basicReject的區別
>
> channel.basicReject(long deliveryTag, boolean requeue)
>
> channel.basicReject比channel.basicNack少了個是否批量操作的參數`multiple`，不能控制是否批量操作

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
        // 獲取當前消息的唯一標識
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 核心操作
            log.info("消費端接收到消息：{}", dataString);
            // 核心操作成功,返回 ACK 資訊
            // deliveryTag: 消息的唯一標識,64 位元的長整型,消息往消費端投遞時,會分配一個唯一的 deliveryTag 值
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 獲取當前消息是否是重複投遞的,true 說明當前消息已經重試過一次了, false 說明當前消息是第一次投遞
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            // 核心操作失敗,返回 NACK 資訊
            // requeue: 是否重新入隊,true 表示重新入隊, false 表示丟棄
            if (redelivered){
                // 如果當前消息已經是重複投遞的,則說明此前已經重試過一次了,則不再重試過了,直接丟棄
                channel.basicNack(deliveryTag, false, false);
            }else {
                // 如果當前消息不是重複投遞的,則說明此前沒有重試過一次,則重試過一次,重新入隊
                channel.basicNack(deliveryTag, false, true);
            }

            throw new RuntimeException(e);
        }
    }
}
```

#### 消息確認相關方法參數說明

**1、delivery Tag: 交付標籤機制**
消費端把消息處理結果ACK、NACK、Reject等返回給Broker之後，Broker需要對對應的消息執行後續操作，例如刪除消息、重新排隊或標記為死信等等。那麼Broker就必須知道它現在要操作的消息具體是哪一條。而delivery Tag作為消息的唯一標識就很好的滿足了這個需求。



提問：如果交換機是Fanout模式，同一個消息廣播到了不同佇列，delivery Tag會重複嗎？

答：不會，deliveryTag在Broker範圍內唯一



思考：更新購物車的微服務消費了消息返回ACK確認資訊，然後Broker刪除了消息，進而導致更新庫存
更新積分的功能拿不到消息一這種情況會發生嗎？



**2、multiple: 是否批量處理**

multiple為 `true` 時，採用批量處理

![image-20241011220119070](assets/67092faf85b10.png)

multiple為`false `時，進行單獨處理

![image-20241011220105475](assets/67092fa1ee2a8.png)

由於批量操作可能導致誤操作，所以一般將`multiple` 設為`false`



**3、requeue：是否重新入隊**

true 表示重新入隊, false 表示丟棄

#### 測試

**1、以Debug模式啟動Consumer服務**

**2、在圖形化介面生成一條消息**

找到`exchange.direct.order`交換機，然後手動發佈一條消息

![image-20241012092733091](assets/6709d0863d22f.png)

消息發佈成功，Debug進入到方法內部

![image-20241012094558111](assets/6709d4d73a565.png)

**3、再查看`queue.order`佇列情況**

發現消息已經被消費尚未ACK確認

![image-20241012095451081](assets/6709d6ebe5c18.png)

**4、消費端正常放行，返回ACK進行確認**

再次查看佇列情況

![image-20241012095857461](assets/6709d7e249d1c.png)

接下來我們模擬異常場景，修改代碼，手動列印 `1/0`使程式出錯，重啟服務

```java
log.info("消費端接收到消息：{}", dataString);
System.out.println(1/0);
```

**1、重新發佈一條消息**

![image-20241012101804618](assets/6709dc5db05bf.png)

**2、debug逐條執行，觀察運行情況**

出現異常被catch捕獲，此時 `redelivered `的值為`false`

![image-20241012101747485](assets/6709dd5b09135.png)

繼續執行，方法進入else ，重新放入佇列

![image-20241012102307570](assets/6709dd8c64f7f.png)

放行，此時消息仍是待確認

![image-20241012102420402](assets/6709ddd54036c.png)

重新進入Debug，繼續逐條執行，這次`redelivered `的值為`true`，不再重試，直接丟棄

![image-20241012102542308](assets/6709de27302dd.png)

放行，此時再查看佇列情況

![image-20241012102828818](assets/6709decda8a13.png)

# 消費端限流

消費端限流可以實現削峰減穀的作用，假設消息總量為1萬條，如果一次性取出所有消息會導致消費端併發壓力過大，我們可以限制**每次最多**從佇列取出1000條消息，這樣就可以對消費端進行很好的保護。

![image-20241012103645282](assets/6709e0be28361.png)

實現也比較簡單，只需添加`prefetch`參數即可

先觀察下預設情況下是如何處理的

**1、我們重寫一個測試方法，生產端發佈100條消息**

```java
@Test
public void test02SendMessage() {
    for (int i = 0; i < 100; i++) {
        String message = "Test Rrefetch!!" + i;
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, message);
    }
}
```

消息發佈後查看下佇列情況

![image-20241012104618929](assets/6709e2fbbc738.png)

2、消費端Listener注釋掉原來的方法，新增一個方法進行處理

```java
@RabbitListener(queues = QUEUE_NAME)
public void processMessage(String dataString, Message message, Channel channel) throws IOException, InterruptedException {
    // 核心操作
    log.info("消費端接收到消息：{}", dataString);

    TimeUnit.SECONDS.sleep(1); //延遲 1 秒

    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

**3、運行消費端服務並查看佇列情況**

觀察發現 `Ready`數量直接從`100`變為`0`，`Unacked`和`Total`隨著消息被消費端消費逐漸減少，說明消費時一次性取出佇列中的所有消息，然後逐條消費。

![image-20241012105500366](assets/6709e50531022.png)

接下來我們限制每次從佇列中獲取的數量並觀察佇列運行情況

**1、添加配置，設置每次從佇列中獲取消息的數量**

```yml
spring:
  rabbitmq:
    listener:
      simple:
        prefetch: 1 # 每次只消費一個消息
```

**2、重新發佈消息以及重啟消費端服務並觀察佇列運行情況**

我們可以看到`Ready`數量每次變化減`5`，這是因為圖形化介面每`5`秒刷新一次

![image-20241012110618114](assets/6709e7aaec766.png)

# 消息超時

給消息設定一個過期時間，超過這個時間沒有被取走的消息就會被刪除
我們可以從兩個層面來給消息設定過期時間：

- 佇列層面：在佇列層面設定消息的過期時間，並不是佇列的過期時間。意思是這個佇列中的消息全部使用同一個過期時間。
- 消息本身：給具體的某個消息設定過期時間
- 如果兩個層面都做了設置，那麼哪個時間短，哪個生效

## 測試

### 給佇列設置超時時間

**1、創建交換機和佇列並建立綁定關係**

交換機：

![image-20241012111226196](assets/6709e91b00fef.png)

佇列：

![image-20241012111351079](assets/6709e96fdf9ac.png)

交換機綁定佇列：

![image-20241012111518137](assets/6709e9c701640.png)

**2、新增測試方法並執行測試**

```java
public static final String EXCHANGE_TIMEOUT = "exchange.test.timeout";
public static final String ROUTING_KEY_TIMEOUT = "routing.key.test.timeout";

@Test
public void test03SendMessage() {
    String message = "Test Timeout!!";
    rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message);
}
```

此時觀察佇列情況，發現`Total`數量從`0`變為`1`，而我們並未運行消費端進行消費，這是因為我們給佇列設置了過期時間，佇列內的消息超出過期時間後被丟棄

![image-20241012112523300](assets/6709ec2431911.png)

### 給消息設置超時時間

**1、刪除原來的佇列並重新創建，不設置超時時間**

佇列：

![image-20241012113853021](assets/6709ef4dcab2d.png)

重新綁定：

![image-20241012113922487](assets/6709ef6b5b631.png)

2、新增測試方法，添加後置處理器物件參數

```java
@Test
public void test04SendMessage() {

    // 創建消息後置處理器物件
    MessagePostProcessor processor = message -> {
        // 設置消息的過期時間為 7 秒
        message.getMessageProperties().setExpiration("7000");
        return message;
    };

    String message = "Test Timeout!!";

    rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message,processor);
}
```

**3、設置`Ack Mode`為`Automatic ack`**

這樣消息處理失敗不會重新加入佇列

![image-20241012114422686](assets/6709f0979a4aa.png)

**4、執行測試方法並觀察佇列情況**

消息超出超時時間後被清除

![image-20241012114732505](assets/6709f15547536.png)

# 死信和無效信件佇列

概念：當一個消息無法被消費，它就變成了死信。
死信產生的原因大致有下面三種：

- 拒絕：消費者拒接消息，basicNack(/basicReject(),並且不把消息重新放入原目標佇列，requeue=false
- 溢出：佇列中消息數量到達限制。比如佇列最大只能存儲10條消息，且現在已經存儲了10條，此時如果再發送一條消息進來，根據先進先出原則，佇列中最早的消息會變成死信
- 超時：消息到達超時時間未被消費

死信的處理方式大致有下面三種：

- 丟棄：對不重要的消息直接丟棄，不做處理
- 入庫：把死信寫入資料庫，日後處理
- 監聽：消息變成死信後進入無效信件佇列，我們專門設置消費端監聽無效信件佇列，做後續處理（通常採用）

## 測試相關準備

### 創建死信交換機和無效信件佇列

- 死信交換機: `exchange.dead.letter.video`
- 無效信件佇列：`queue.dead.letter.video`
- 死信路由鍵：`routing.key.dead.letter.video`

### 創建正常交換機和正常佇列

> 注意：一定要注意正常佇列有諸多限定和設置，這樣才能讓無法處理的消息進入死信交換機
>
> x-dead-letter-exchange: 關聯的死信交換機
>
> x-dead-letter-routing-key：關聯的死信路由鍵
>
> x-max-length：佇列最大容量長度
>
> x-message-ttl：佇列超時時間

![image-20241012120513217](assets/6709f57a2079d.png)

- 正常交換機：`exchange.normal.video`
- 正常佇列: `queue.normal.video`
- 正常路由鍵：`routing.key.normal.video`



### java代碼中的相關常量聲明

```java
public static final String EXCHANGE_NORMAL = "exchange.normal.video";
public static final String EXCHANGE_DEAD_LETTER = "exchange.dead.letter.video";
    
public static final String ROUTING_KEY_NORMAL = "routing.key.normal.video";
public static final String ROUTING_KEY_DEAD_LETTER = "routing.key.dead.letter.video";
    
public static final String QUEUE_NORMAL = "queue.normal.video";
public static final String QUEUE_DEAD_LETTER = "queue.dead.letter.video";
```



## 消費端拒收消息

### 發送消息的代碼

> 也可直接在圖形化介面操作

```java
@Test
public void testSendRejectMessage() {
    rabbitTemplate.convertAndSend(EXCHANGE_NORMAL, ROUTING_KEY_DEAD_LETTER, "測試死信情況1:消息被拒絕");
}
```

### 接收消息的代碼

> 由於監聽正常佇列的方法一定會拒絕並且不會重新加入佇列，那麼佇列中的消息就會成為死信並加入到無效信件佇列中，無效信件佇列正常返回。

① 監聽正常佇列

```java
/**
* 監聽正常佇列
*/
@RabbitListener(queues = QUEUE_NORMAL)
public void processNormalMessage(Message message, Channel channel) throws IOException {
    // 監聽正常佇列,但是拒絕消息
    log.info("★[normal] 消息接收到,但我拒絕。");
    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
}
```

② 監聽無效信件佇列

```java
/**
* 監聽無效信件佇列
*/
@RabbitListener(queues = QUEUE_DEAD_LETTER)
public void processDeadMessage(String dataString, Message message, Channel channel) throws IOException {
    //監聽無效信件佇列
    log.info("★[dead letter] dataString = " + dataString);
    log.info("★[dead1 etter] 我是死信監聽方法,我接收到了死信消息");
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

### 執行結果

**1、正常佇列發佈消息**

![image-20241012124002121](assets/6709fda2dfd30.png)

**2、重啟消費端服務**

後臺日誌輸出情況

![image-20241012124439157](assets/6709feb7e0e1a.png)

**3、觀察佇列情況**

正常佇列：

![image-20241012124716010](assets/6709ff54bc1cb.png)

無效信件佇列：

![image-20241012124709660](assets/6709ff4e73188.png)

## 消費數量超過佇列容量極限

### 發送消息的代碼

```java
@Test
public void testSendMultiMessage() {
    for (int i = 0; i < 20; i++) {
        rabbitTemplate.
            convertAndSend(
            EXCHANGE_NORMAL,
            ROUTING_KEY_NORMAL,
            "測試死信情況2:數量超過佇列最大容量" + i);
    }
}
```

### 接收消息的代碼

### 執行效果

**1、停止消費端服務，批量發送20條消息**

**2、觀察佇列情況**

正常佇列：

由於我們設置的最容量為`10`，所以我們最多接收`10`條消息，超出設定的超時時間後消息被廢棄，數量變為`0`

![image-20241012125548969](assets/670a0155bd920.png)

無效信件佇列：

由於我們設置的最大容量為`10`，消息成為死信後每`10`條消息為一個批次加入無效信件佇列

![image-20241012125605016](assets/670a0165cecc9.png)

此時我們啟動消費端服務，觀察日誌輸出情況，可以發現都是`dead`級別的日誌，因為此時佇列裡的所有消息都變為死信了。

![image-20241012130701315](assets/670a03f652392.png)

## 消息超時未消費

### 發送消息的代碼

> 由於我們設置的佇列最大容量為10，為了避免由於溢出產生死信的影響，我們發送小於10條的資料

```java
@Test
public void testSendDelayMessage() {
    for (int i = 0; i < 8; i++) {
        rabbitTemplate.
            convertAndSend(
            EXCHANGE_NORMAL,
            ROUTING_KEY_NORMAL,
            "測試死信情況3:消息超時未消費" + i);
    }
}
```

### 執行效果

**1、停止消費端服務，發送消息**

**2、查看佇列情況**

正常佇列：

![image-20241012133737142](assets/670a0b21d5eb5.png)

無效信件佇列：

無效信件佇列從原始的`30`條數量增至`38`條，我們發送的`8`條資料因為超時未消費加入到無效信件佇列中

![image-20241012133715332](assets/670a0b0c29ff9.png)

# 延遲佇列

## 業務場景

在限定時間內進行支付，否則訂單自動取消

![image-20241012134202272](assets/670a0c2b08952.png)

## 實現思路

### **方案1：設置消息超時時間 + 無效信件佇列**

> 可參考上文介紹，不再演示

![image-20241012134438561](assets/670a0cc76665e.png)

### **方案2：給RabbitMQ安裝外掛程式**

#### 外掛程式介紹

官網地址：https:/github.com/rabbitmq/rabbitmq-delayed-message-exchange
延遲極限：最多兩天

#### 安裝外掛程式

#### 確定卷映射目錄

```shell
docker inspect rabbitmq
```

運行結果：

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

查看`Mounts`中Name為`rabbitmq-plugin`對應的`Source`值

可以看到值為`/var/lib/docker/volumes/rabbitmq-plugin/_data`

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

#### 下載延遲外掛程式

官方文檔說明頁地址：https://www.rabbitmq.com/community-plugins

**rabbitmq_delayed_message_exchange**

A plugin that adds delayed-messaging (or scheduled-messaging) to RabbitMQ.

- [Releases](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases)
- Author: **Alvaro Videla**
- GitHub: [rabbitmq/rabbitmq-delayed-message-exchange](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)



下載外掛程式安裝檔：

```shell
cd /var/lib/docker/volumes/rabbitmq-plugin/_data
wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez
```

若連接被拒絕可多次嘗試，或手動下載

![image-20241012141305073](assets/670a1371eaf32.png)

### 啟用外掛程式

```shell
# 登錄進入容器內部
docker exec -it rabbitmq /bin/bash

# rabbitmq-plugins命令所在目錄已經配置到$PATH環境變數中了,可以直接調用
rabbitmq-plugins enable rabbitmq_delayed_message_exchange

# 查看外掛程式列表，檢查外掛程式是否啟用 有E*標識即為已啟用
# [E*] rabbitmq_delayed_message_exchange 3.13.0
rabbitmq-plugins list

# 退出Docker容器
exit

# 重啟Docker容器
docker restart rabbitmq
```



#### 確認

確認點1：查看當前節點已啟用外掛程式的列表：

![image-20241012142358715](assets/670a15ff657d8.png)

確認點2：如果創建新交換機時在`Type`中可以看到`x-delayed-message`選項，則說明外掛程式安裝好了

![image-20241012143937188](assets/670a19aadf4f8.png)

### 創建交換機及佇列

**創建交換機：**

Type選擇`x-delayed-message`，添加`x-delayed-type`來指定切換類型

![image-20241012142648189](assets/670a16a8d31da.png)

**創建佇列：**

![image-20241012142901241](assets/670a172def313.png)

**佇列綁定交換機：**

![image-20241012143149354](assets/670a17d60254c.png)

### 代碼測試

#### 生產者端代碼

```java
public static final String EXCHANGE_DELAY = "exchange.test.delay";
public static final String ROUTING_KEY_DELAY = "routing.key.test.delay";

@Test
public void sendDelayMessageByPlugin() {
    // 創建消息後置處理器物件
    MessagePostProcessor processor = message -> {
        // x-delay: 消息的過期時間 (單位:毫秒)
        // 安裝 rabbitmq_delayed_message_exchange 外掛程式才生效
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

#### 消費者端代碼

```java
public static final String QUEUE_DELAY = "queue.test.delay";

@RabbitListener(queues = {QUEUE_DELAY})
public void processDelayMessage(String dataString, Message message, Channel channel) throws IOException {
    //監聽無效信件佇列
    log.info("[delay message] [消息本身] " + dataString);
    log.info("[delay message] [當前時間] " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

啟動消費者端服務並發送消息，查看日誌輸出情況

![image-20241012150040029](assets/670a1e98aa942.png)

注意：啟用外掛程式後，returnedMessage方法始終會執行

![image-20241012150258397](assets/670a1f2319d8d.png)

# 事務消息

> RabbitMQ的事務只是作用到生產者端，而且只起到局部作用

RabbitMQ的事務功能非常有限，只是控制是否將緩存中的消息發送到Broker，並不能保證消息的可靠性投遞

![image-20241012151455965](assets/670a21f0d0051.png)

## 實操演示

### 環境準備

#### 創建項目並導入依賴

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

#### 設定檔

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

#### 啟動類

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

#### 配置類

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

#### 測試類

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
        // 1、 發送一條消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 1");

        // 2、拋出異常
        log.info("do bad: "+ 10/0);

        // 3、發送第二條消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 2");
    }
}
```



### 測試

> 我們分別發送兩條消息，兩條消息中間手動拋出異常，來觀察啟用事務前後的區別

**1、創建交換機、佇列並綁定關係**

交換機名稱：exchange.tx.dragon

佇列名稱：queue.test.tx

路由鍵：routing.key.tx.dragon

**2、發送消息並觀察佇列情況**

預設未使用事務的情況：第一條事務發送成功，消息能夠正常獲取



![image-20241012155135362](assets/670a2a8802691.png)

開啟事務：

測試類添加`@Transactional`注解，由於JUnit中是默認回滾的，我們想要提交事務，需要添加`@Rollback(value = false)`注解

```java
@Test
@Transactional
//@Rollback(value = false)
public void testSendMessageTx(){
    // 1、 發送一條消息
    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 1");

    // 2、拋出異常
    log.info("do bad: "+ 10/0);

    // 3、發送第二條消息
    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 2");
}
```

我們保持預設回滾事務，執行測試方法，觀察佇列情況

由於出現異常，事務被回滾，消息未發送

![image-20241012160804246](assets/670a2e64e3dbc.png)

# 惰性佇列

惰性佇列：未設置惰性模式時佇列的持久化機制

創建佇列時，在Durabilityi這裡有兩個選項可以選擇

- Durable: 持久化佇列，消息會持久化到硬碟上
- Transient: 臨時佇列，不做持久化操作，broker重啟後消息會丟失

![image-20241012161104810](assets/670a2f1969856.png)

> 思考：Durable佇列在存入消息之後，是否是立即保存到硬碟呢？

其實並不會立即保存到硬碟，當記憶體中的佇列達到一定容量或者Broker關閉時才會保存到硬碟

![image-20241012161258471](assets/670a2f8b26856.png)



官網上對於惰性佇列的介紹

![image-20241012161555727](assets/670a303c6f78a.png)



比較下面兩個說法是否是相同的意思：

- 立即移動到硬碟
- 儘早移動到硬碟

理解：

- 立即：消息剛進入佇列時

- 儘早：伺服器不繁忙時

  

惰性佇列應用場景

![image-20241012161831032](assets/670a30d7b419f.png)

原文翻譯：使用惰性佇列的主要原因之一是支援非常長的佇列（數百萬條消息）
由於各種原因，排隊可能會變得很長：

- 消費者離線/崩潰/停機進行維護
- 突然出現消息進入高峰生產者的速度超過了消費者
- 消費者比正常情況慢

# 優先順序佇列

機制說明
預設情況：基於佇列先進先出的特性，通常來說，先入隊的先投遞
設置優先順序之後：優先順序高的消息更大幾率先投遞
關鍵參數：`x-max-priority`

RabbitMQ允許我們使用一個正整數給消息設定優先順序
消息的優先順序數值取值範圍：`1~255`
RabbitMQ官網建議在`1~5`之間設置消息的優先順序（優先順序越高，佔用CPU、記憶體等資源越多)

佇列在聲明時可以指定參數：`x-max-priority`
預設值：`0` ，此時消息即使設置優先順序也無效
指定一個正整數值：消息的優先順序數值不能超過這個值

## 實操演示

**1、創建交換機及佇列並綁定**

交換機名稱：exchange.test.priority

佇列名稱：queue.test.priority

> x-max-priority的類型必須是Number

![image-20241012163146628](assets/670a33f343577.png)

路由鍵：routing.key.test.priority

**2、分別發送三條消息，優先順序從低到高，後面觀察入隊情況**

```java
public static final String EXCHANGE_PRIORITY = "exchange.test.priority";
public static final String ROUTING_KEY_PRIORITY = "routing.key.test.priority";
```

發送第一條消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 1",message -> {
            //消息本身的優先級數據,不能超過佇列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(1);
            return message;
        });
}
```

發送第二條消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 2",message -> {
            //消息本身的優先級數據,不能超過佇列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(2);
            return message;
        });
}
```

發送第三條消息

```java
@Test
public void testSendPriorityMessage() {
    rabbitTemplate.
        convertAndSend(
        EXCHANGE_PRIORITY,
        ROUTING_KEY_PRIORITY,
        "Test Priority Message 3",message -> {
            //消息本身的優先級數據,不能超過佇列配置的最大值 x-max-priority
            message.getMessageProperties().setPriority(3);
            return message;
        });
}
```

**3、啟動用戶端服務，查看日誌輸出情況**

```java
public static final String QUEUE_PRIORITY = "queue.test.priority";

@RabbitListener(queues = {QUEUE_PRIORITY})
public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
    log.info("[priority]: " + dataString);
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

我們可以看到優先順序高的先輸出

![image-20241012165314324](assets/670a38faf33bd.png)

# 集群搭建

## 安裝RabbitMQ

### 前置要求

> 課程要求CentOS發行版本的版本≥8，CentOS 7.x  其實也可以，後面有詳細介紹

下載地址：https://mirrors.163.com/centos/

查看當前系統發行版本本：

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



RabbitMQ安裝方式官方指南：

> https://www.rabbitmq.com/docs/install-rpm

![image-20241012170624420](assets/670a3c110e06a.png)

### 安裝Erlang環境

#### **創建yum庫設定檔**

```shell
vim /etc/yum.repos.d/rabbitmq.repo
```

#### **加入配置內容**

> 以下內容來自官網文檔：https://www.rabbitmq.com/docs/install-rpm

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

#### **更新yum庫**

> --nobest 表示所需安裝包即使不是最佳選擇也接收

```shell
yum update -y --nobest
```

若不支援系統`--nobest`參數則可不使用

```shell
yum update -y 
```

![image-20241012171955556](assets/670a3f3c5005e.png)

#### **正式安裝Erlang**

##### CentOS 8


```shell
yum install -y erlang
```

##### **CentOS 7**

**卸載舊版本**

若未安裝過，可跳過

> **卸載舊版本的 Erlang**
>
> 1. **查找已安裝的 Erlang 包：**
>
>    ```bash
>    rpm -qa | grep erlang
>    ```
>
> 2. **卸載舊版本的 Erlang**：
>
>    ```bash
>    sudo yum remove erlang-26.2.5.4-1.el7.x86_64
>    ```
>
> **檢查並刪除殘留檔**
>
> **確保系統中沒有其他 Erlang 版本的殘留檔或配置。**
>
> 1. **查找並刪除所有與 Erlang 相關的目錄**：
>
>    ```bash
>    sudo find / -name "erlang" -type d -exec rm -rf {} +
>    ```
>
> 2. **查找並刪除所有與 Erlang 相關的檔**：
>
>    ```bash
>    sudo find / -name "*erlang*" -type f -exec rm -f {} +
>    ```
>
> 3. **查找並刪除所有與 Erlang 相關的符號連結**：
>
>    ```bash
>    sudo find /usr/bin /usr/local/bin -name "erl*" -type l -exec rm -f {} +



安裝時需要注意Erlang與CentOS的版本匹配，詳細介紹見官網： https://www.rabbitmq.com/docs/which-erlang

![image-20241013120828545](assets/670b47bc9a9df.png)

如課程中RabbitMQ使用的是`v3.13.0`，erlang需要安裝的版本需要 >= 26.0

由於`rabbitmq-server` 安裝包支持CentOS7的版本較老，如 `v3.9.16`，相容的erlang最低版本為23.3，最高24.3

![image-20241013122208421](assets/670b4af04aeb2.png)



**通過RPM安裝**

> 可參考文章：[OpenCloudOS 8配置rabbitmq](https://blog.csdn.net/MeltryLL/article/details/141437375)

下載地址：https://github.com/rabbitmq/erlang-rpm/releases

我們需要下載與之相相容的erlang版本如 [erlang-23.3-2.el7.x86_64.rpm](https://github.com/rabbitmq/erlang-rpm/releases/tag/v23.3)， el7 代表 CentOS 7

GitHub倉庫地址： https://github.com/rabbitmq/erlang-rpm/releases

![image-20241013122538176](assets/670b4bc1f1519.png)

將檔上傳到CentOS的某個目錄上，如`/opt/rabbitmq`

```bash
# 安裝
sudo rpm -ivh erlang-23.3-2.el7.x86_64.rpm

# 檢查 Erlang 版本，驗證 Erlang 是否安裝成功
# Erlang (SMP,ASYNC_THREADS,HIPE) (BEAM) emulator version 11.2
erl -version

# 或者用erl命令,其中OTP 23是我們安裝的版本,erts-11.2是lib庫依賴的版本
#Erlang/OTP 23 [erts-11.2] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1] [hipe]

#Eshell V11.2  (abort with ^G)
erl
```



**通過yum 安裝**


> 可參考文章： [CentOS 7 安裝Erlang、RabbitMQ（親測通過）](https://blog.csdn.net/mumanquan1/article/details/122074059)

Erlang安裝包下載地址： https://packagecloud.io/rabbitmq/erlang 

選擇與`rabbitmq-server` 相相容的版本，如 `erlang-23.3.4.11-1.el7.x86_64.rpm`，el7 代表適用CentOS7

> 若執行第一步出現如下錯誤
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
> 檢查Python版本
>
> ```bash
> [root@localhost ~]# python --version
> Python 3.7.0
> ```
>
> 若為3.x，執行如下命令創建軟連接，使用python2執行
>
> ```
> sudo ln -sf /usr/bin/python2 /usr/bin/python
> ```

```shell
# 步驟 1：安裝了存儲庫
curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash

# 步驟 2：安裝套裝軟體
sudo yum install -y erlang-23.3.4.11-1.el7.x86_64 
```

若下載失敗可到官網手動下載安裝

下載地址：https://www.erlang.org/downloads，會跳轉至GitHub

GitHub: https://github.com/erlang/otp/releases

![image-20241013130235515](assets/670b546b5e5c4.png)

下載完成後，將檔上傳到某個目錄，如`/opt/rabbitmq`，通過以下代碼完成安裝

```bash
# 使用 yum 包管理器安裝 GCC 編譯器，-y 選項表示自動回答 "yes" 以確認所有提示
yum -y install gcc

# 使用 tar 命令解壓 Erlang 源碼包，-z 選項表示使用 gzip 解壓，-x 選項表示解壓，-v 選項表示顯示詳細資訊，-f 選項指定檔案名
tar -zxvf otp_src_23.3.4.11.tar.gz

# 進入解壓後的 Erlang 源碼目錄
cd /opt/rabbitmq/otp_src_23.3.4.11/

# 運行 configure 腳本，--prefix 選項指定 Erlang 的安裝路徑為 /usr/local/erlang
./configure --prefix=/usr/local/erlang

# 編譯並安裝 Erlang，make install 會執行編譯後的安裝步驟
make install
```

查看是否安裝成功以及設置環境變數

```shell
# 列出 /usr/local/erlang/bin 目錄下的所有檔和目錄，ll 是 ls -l 的別名，顯示詳細資訊
ll /usr/local/erlang/bin

# 將 Erlang 的 bin 目錄添加到系統的 PATH 環境變數中，以便在終端中可以直接使用 Erlang 命令
echo 'export PATH=$PATH:/usr/local/erlang/bin' >> /etc/profile

# 重新載入 /etc/profile 檔，使環境變數配置立即生效
source /etc/profile

# 檢查 Erlang 版本，驗證 Erlang 是否安裝成功
# Erlang (SMP,ASYNC_THREADS,HIPE) (BEAM) emulator version 11.2.2.10
erl -version

# 或者用 erl 驗證
# Erlang/OTP 23 [erts-11.2.2.10] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1] #[hipe]

#Eshell V11.2.2.10  (abort with ^G)
#1>

erl
```



> 安裝Erlang最新版會遇到的坑

此處發現列印的是版本是 `14.2.5.4`

```bash
erl -version
Erlang (SMP,ASYNC_THREADS) (BEAM) emulator version 14.2.5.4
```

使用 `erl`驗證下，發現

```bash
[root@localhost rabbitmq]# erl
Erlang/OTP 26 [erts-14.2.5.4] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1]

Eshell V14.2.5.4 (press Ctrl+G to abort, type help(). for help)
1>
```

安裝RabbitMQ時提示如下錯誤

```bash
[root@localhost rabbitmq]# rpm -ivh rabbitmq-server-3.13.0-1.el8.noarch.rpm 錯誤：依賴檢測失敗：        erlang >= 26.0 被 rabbitmq-server-3.13.0-1.el8.noarch 需要
```

### **安裝RabbitMQ**

#### CentOS 8

```shell
# 導入GPG金鑰
rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc'

rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key'

rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key'

# 下載 RPM 包
# 若下載失敗多嘗試幾次或CentOS重啟後重新嘗試
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.13.0/rabbitmq-server-3.13.0-1.el8.noarch.rpm

# 安裝
rpm -ivh rabbitmq-server-3.13.0-1.el8.noarch.rpm
```

#### CentOS 7
通過[Release Information | RabbitMQ](https://www.rabbitmq.com/release-information)跳轉到github下載介面

https://github.com/rabbitmq/rabbitmq-server/releases

選擇與`rabbitmq-server` 相相容的版本，如  [rabbitmq-server-3.9.16-1.el7.noarch.rpm](https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.9.16)

![image-20241013002916641](assets/670aa3dd08f44.png)

上傳到CentOS某個目錄，如 `/opt/rabbitmq`

```bash
# 安裝
rpm -ivh rabbitmq-server-3.9.16-1.el7.noarch.rpm

# 顯示如下資訊代表安裝成功
#警告：rabbitmq-server-3.9.16-1.el7.noarch.rpm: 頭V4 RSA/SHA512 Signature, 金鑰 ID 6026dfca: #NOKEY
#準備中...                          ################################# [100%]
#正在升級/安裝...
#   1:rabbitmq-server-3.9.16-1.el7     ################################# [100%]
```

### **RabbitMQ基礎配置**

> 啟動服務前注意停用之前的Docker服務，以免造成埠衝突

```shell
# 啟用管理介面外掛程式
rabbitmq-plugins enable rabbitmq_management

# 啟動 RabbitMQ 服務
systemctl start rabbitmq-server

# 將 RabbitMQ 服務設置為開機自動啟動
systemctl enable rabbitmq-server

# 新增登錄帳號密碼
rabbitmqctl add_user shiguang 123456

# 設置登錄帳號許可權
rabbitmqctl set_user_tags shiguang administrator
rabbitmqctl set_permissions -p / shiguang ".*" ".*" ".*"

# 設置所有穩定功能 flag 啟動
rabbitmqctl enable_feature_flag all

# 重新開機 RabbitMQ服務
systemctl restart rabbitmq-server
```

### 收尾工作

> 若不刪除該配置，以後用yum安裝會受到該配置影響

```bash
rm -rf /etc/yum.repos.d/rabbitmq.repo
```

## 克隆 VMWare虛擬機器

### 目標

通過克隆操作，一共準備三台VMWare虛擬機器

| 集群節點名稱 | 虛擬機器IP地址  |
| ------------ | ------------- |
| node01       | 192.168.10.66 |
| node02       | 192.168.10.88 |
| node03       | 192.168.10.99 |

### 克隆虛擬機器

需克隆完整連接

![image-20241013132556306](assets/670b59e4277f9.png)

需要

![image-20241013132634295](assets/670b5a0a12674.png)

### 給新機器設置IP位址

在CentOS 7 中，可以使用`nmcli`命令列工具修改IP位址。以下是具體步驟：

1、查看網路連接資訊：

```bash
nmcli con show 
```

2、停止指定的網路連接（將 <connection_name>替換為實際的網路連接名稱）：

```bash
nmcli con down <connection_name>
```

3、修改IP位址（將 <connection_name>替換為實際的網路連接名稱，將 <new_ip_address>替換為新的IP位址，將<subnet_mask>替換為子網路遮罩，將\<gateway>替換為閘道）

```bash
# <new_ip_address>/<subnet_mask>這裡是 CIDR 標記法
nmcli con mod <connection_name> ipv4.addresses <new_ip_address>/<subnet_mask>
nmcli con mod <connection_name> ipv4.gateway <gateway>
nmcli con mod <connection_name> ipv4.method manual # 手動
```

4、啟動網路連接

```bash
nmcli con up <connection_name>
```

5、驗證新的IP位址是否生效：

```bash
ip addr show
```

### 修改主機名稱稱

主機名稱稱會被RabbitMQ作為集群中的節點名稱，後面會用到，所以需要設置一下。
修改後需重啟

```bash
# 查看當前系統名稱
cat /etc/hostname
# 修改當前系統名稱
vim /etc/hostname
```

### 保險措施

為了在後續操作過程中，萬一遇到操作失誤，友情建議拍攝快照。

## 集群節點彼此發現

### node01設置

① 設置IP位址到主機名稱稱的映射

修改檔`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下內容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 查看當前RabbitMQ節點的Cookie值並記錄

```bash
cat /var/lib/rabbitmq/.erlang.cookie
```

顯示如下：

```bash
[root@node01 ~]# cat /var/lib/rabbitmq/.erlang.cookie
KFGJAHXELTVBZJVTEHSG[root@node01 ~]#
```

③ 重置節點應用

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app
```

### node02設置

① 設置P位址到主機名稱稱的映射

修改檔`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下內容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 修改當前RabbitMQ節點的Cookie值
node02和node03都改成和node01一樣：

```bash
vim /var/lib/rabbitmq/.erlang.cookie
```

③ 重置節點應用並加入集群

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@node01
rabbitmqctl start_app
```

### node03設置

① 設置P位址到主機名稱稱的映射

修改檔`/etc/hosts`

```bash
vim /etc/hosts
```

追加如下內容：

```bash
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```

② 修改當前RabbitMQ節點的Cookie值
node02和node03都改成和node01一樣：

```bash
vim /var/lib/rabbitmq/.erlang.cookie
```

③ 重置節點應用並加入集群

```bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@node01
rabbitmqctl start_app
```

④ 查看集群狀態

```bash
rabbitmqctl cluster_status
```

顯示如下：

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

也可登錄管理後臺查看

![image-20241013150943009](assets/670b7236e8402.png)



## 負載均衡：Management UI

### 說明

兩個需要暴露的埠：

![image-20241013151148707](assets/670b72b46ef57.png)

目前集群方案：

![image-20241013151250667](assets/670b72f26a90b.png)

管理介面負載均衡：

![image-20241013151402696](assets/670b733a7ea3f.png)

核心功能負載均衡：

![image-20241013151456881](assets/670b7370b02e3.png)

### 安裝HAProxy

```bash
# 安裝
yum install -y haproxy

# 檢查是否安裝成功
haproxy -v

# 啟動
systemctl start haproxy

# 設置開機自啟動
systemctl enable haproxy
```

### 修改設定檔

> 設定檔位置：/etc/haproxy/haproxy.cfg

在設定檔未尾增加如下內容：

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

設置SELinux策略，允許HAProxy擁有許可權連接任意埠：

> SELinux是Linux系統中的安全模組，它可以限制進程的許可權以提高系統的安全性。在某些情況下，SELinux可能會阻止HAProxy綁定指定的埠，這就需要通過設置域(domain)的安全性原則來解決此問題。
>
> 通過執行setsebool-P haproxy_connect_any=1命令，您已經為HAProxyi設置了一個布林值，允許HAProxy連接到任意埠。這樣，HAProxy就可以成功綁定指定的socket,並正常工作。

```bash
setsebool -P haproxy_connect_any=1
```


重啟HAProxy

```bssh
systemctl restart haproxy
```

### 測試效果

訪問配置的前臺負載均衡位址： http://192.168.10.66:22222

查看是否可以正常打開rabbitmq管理端介面

![image-20241013153928411](assets/670b793069de5.png)

## 負載均衡：核心功能

### 添加配置

> 設定檔位置：/etc/haproxy/haproxy.cfg

在設定檔未尾增加如下內容：

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

重啟HAProxy

```bssh
systemctl restart haproxy
```

### 測試

#### **創建組件**

- 交換機：exchange.cluster.test
- 佇列；queue.cluster.test
- 路由鍵：routing.key.cluster.test

#### **創建生產者端程式**

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

**2、核心設定檔**

```yml
spring:
  rabbitmq:
    host: 192.168.10.66
    port: 11111
    username: shiguang
    password: 123456
    virtual-host: /
    publisher-confirm-type: CORRELATED #交換機的確認
    publisher-returns: true #佇列的確認
logging:
  level:
    com.shiguang.mq.config.MQProducerAckConfig: info
```

**3、配置類**

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
     * 消息發送到交換機成功或失敗時調用這個方法
     *
     * @param correlationData 用於關聯消息的唯一識別碼
     * @param ack             表示消息是否被成功確認
     * @param cause           如果消息確認失敗，這裡會包含失敗的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息發送到交換機成功！數據: " + correlationData);
        } else {
            log.info("消息發送到交換機失敗！ 數據: " + correlationData + " 錯誤原因: " + cause);
        }

    }

    /**
     * 當消息無法路由到佇列時調用這個方法
     *
     * @param returnedMessage 包含無法路由的消息的詳細資訊
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage() 回呼函數 消息主體: " + new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回呼函數 應答碼: " + returnedMessage.getReplyCode());
        log.info("returnedMessage() 回呼函數 描述: " + returnedMessage.getReplyText());
        log.info("returnedMessage() 回呼函數 消息使用的交換器 exchange: " + returnedMessage.getExchange());
        log.info("returnedMessage() 回呼函數 消息使用的路由鍵 routing: " + returnedMessage.getRoutingKey());
    }
}
```

**4、啟動類**

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

**5、測試類**

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



#### 創建消費者端程式

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

**2、核心設定檔**

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
        acknowledge-mode: manual #手動確認
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
        log.info("[消費者端] 消息內容: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
```

**4、啟動類**

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

#### 運行結果

![image-20241013164328717](assets/670b883067356.png)

## 鏡像佇列

> 鏡像佇列在新版本中已被仲裁佇列取代，這裡不再介紹

## 仲裁佇列

RabbitMQ3.8.x版本的主要更新內容，未來有可能取代Classic Queue

創建仲裁佇列，可以將佇列同步到集群中的每個節點上

![image-20241013164832214](assets/670b895fe4c73.png)

### 操作步驟

#### 創建仲裁佇列

> 需要在集群的基礎上創建

**1、創建交換機**

和仲裁佇列綁定的交換機沒有特殊要求，我們還是創建一個direct交換機即可
交換機名稱：exchange.quorum.test

**2、創建仲裁佇列**

佇列名稱：queue.quorum.test

![image-20241013165350867](assets/670b8a9e7fddc.png)

創建好後如圖所示：

![image-20241013165518171](assets/670b8af5cbc51.png)

詳情信息：

![image-20241013165545104](assets/670b8b10c7469.png)



3、綁定交換機

路由鍵：routing.key.quorum.test

#### 測試

##### 常規測試

像使用經典佇列一樣發送消息、消費消息

**① 生產者端**

```java
public static final String EXCHANGE_QUORUM_TEST = "exchange.quorum.test";
public static final String ROUTING_KEY_QUORUM_TEST = "routing.key.quorum.test";

@Test
public void testSendMessageToQuorum() {

    String message = "Test Send Message By Quorum!!";

    rabbitTemplate.convertAndSend(EXCHANGE_QUORUM_TEST, ROUTING_KEY_QUORUM_TEST, message);
}

```

日誌輸出情況：

![image-20241013170259621](assets/670b8cc33938f.png)

佇列情況：

![image-20241013170225207](assets/670b8ca0d734a.png)



**② 消費者端**

```java
public static final String QUEUE_QUORUM_TEST = "queue.quorum.test";

@RabbitListener(queues = {QUEUE_QUORUM_TEST})
public void processQuorumMessage(String dataString, Message message, Channel channel) throws IOException {
    log.info("[消費者端] 消息內容: " + dataString);
    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
}
```

日誌輸出情況：

![image-20241013170711764](assets/670b8dbf5bb45.png)

佇列情況：

![image-20241013170736519](assets/670b8dd82bbb1.png)



##### 高可用測試

① 停止某個節點的rabbit應用

```bash
# 停止rabbit應用
rabbitmqctl stop_app
```

此時可以再觀察下佇列詳情，可以發現已自動選舉出新的Leader

![image-20241013171039544](assets/670b8e8f41fb3.png)



② 再次發送消息

修改發送消息的內容，以區分之前發送的消息，消費者端能夠正常消費

控制台有報錯是因為有節點下線，屬於正常情況

![image-20241013171344976](assets/670b8f48b9a4b.png)



## 流式佇列

RabbitMQ在 3.9.x 推出的新特性

**工作機制**：

在一個僅追加的日誌檔內保存所發送的消息

![image-20241013171615604](assets/670b8fdf3b1e5.png)

給每個消息都分配個偏移頁，即使消息被消費端消費掉，消息依然保存在日誌檔中，可重複消費



![image-20241013171638560](assets/670b8ff637f5a.png)

**總體評價**

- 從用戶端支援角度來說，生態尚不健全
- 從使用習慣角度來說，和原有佇列用法不完全相容
- 從競品角度來說，**像Kafka，但遠遠比不上Kafka**
- 從應用場景角度來說：
  - 經典佇列：適用於系統內部非同步通信場景
  - 流式佇列：適用於系統間跨平臺、大流量、即時計算場景(Kafka主場)
- 使用建議：Stream佇列在目前企業實際應用非常少，真有特定場景需要使用肯定會傾向於使用Kafka,而不是RabbitMQ Stream
- 未來展望：Classic Queue已經有和Quorum Queue合二為一的趨勢,Stream也有加入進來整合成一種佇列的趨勢，但Stream內部機制決定這很難

### 使用步驟

#### 啟用外掛程式

> 說明：只有啟用了Stream外掛程式，才能使用流式佇列的完整功能

在集群每個節點中依次執行如下操作：

```bash
# 啟用Stream外掛程式
rabbitmq-plugins enable rabbitmq_stream 

# 重啟rabbit應用
rabbitmqctl stop_app
rabbitmqctl start_app

# 查看外掛程式狀態
rabbitmq-plugins list
```

![image-20241013201150462](assets/670bb9061efb6.png)

#### 負載均衡

> 設定檔位置：/etc/haproxy/haproxy.cfg

在設定檔未尾增加如下內容：

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

重啟HAProxy

```bssh
systemctl restart haproxy
```

#### JAVA代碼

Stream專屬Java用戶端官方網址：https://github.com/rabbitmq/rabbitmq-stream-java-client
Stream專屬Java用戶端官方文檔網址：https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/htmlsingle/

##### 引入依賴

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

##### 創建Stream

> 不需要創建交換機

**① 代碼方式創建**

```java
Environment environment = Environment.builder()
    .host("192.168.10.66")
    .port(33333)
    .username("shiguang")
    .password("123456")
    .build();

environment.streamCreator().stream("stream.shiguang.test").create();
```



**② ManagementUlt創建**

![image-20241013202817496](assets/670bbce107546.png)

##### 生產端程式

**① 內部機制說明**
[1] 官方文檔

> Internally，the Environment will query the broker to find out about the topology of the stream and will create or re-use a connection to publish to the leader node of the stream.

翻譯：

> 在內部，Environment將查問brokerl以瞭解流的拓撲結構，並將創建或重用連接以發佈到流的leader節點。

[2] 解析

- 在Environment中封裝的連接資訊僅負責連接到 broker
- Producer在構建物件時會訪問broker拉取集群中 Leader 的連接資訊
- 將來實際訪問的是集群中的 Leader 節點
- Leader的連接資訊格式是：節點名稱:埠號

![image-20241013203356647](assets/670bbe344e090.png)

[3] 配置

> 檔位置： C:\Windows\System32\drivers\etc

為了讓本機的應用程式知道Leader節點名稱對應的IP位址，我們需要在**本地**配置hosts檔，建立從節點名稱到P地址的映射關係

```tex
# rabbitmq 測試
192.168.10.66 node01
192.168.10.88 node02
192.168.10.99 node03
```



**② 示例代碼**

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
            System.out.println("[生產者端]the message made it to the broker");
        } else {
            System.out.println("[生產者端]the message did not make it to the broker");
        }
        countDownLatch.countDown();
    });
countDownLatch.await();
producer.close();
environment.close();
```

##### 消費端程式

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
        System.out.println("[消費者] messagecontent = " + messageContent + " offset = " + offset.offset());
    })
    .build();
```

#### 指定偏移量消費

##### 偏移量

![image-20241013211641886](assets/670bc8395c066.png)

##### 官網文檔說明

![image-20241013211716855](assets/670bc85c7e1d5.png)

##### 指定Offset消費

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
        System.out.println("[消費者端] messagecontent = " + messageContent);
        countDownLatch.countDown();
    })
    .build();

countDownLatch.await();
consumer.close();
```

##### 對比

- autoTrackingStrategy方式：始終監聽Stream中的新消息（狗狗看家，忠於職守）
- 指定偏移量方式：針對指定偏移量的消息消費之後就停止（狗狗叼飛盤，叼回來就完）

# Federation外掛程式

## 簡介

Federation外掛程式的設計目標是使RabbitMQ在不同的Broker節點之間進行消息傳送而無須建立集群。

它可以在不同的管理域中的Broker或集群間傳遞消息，這些管理域可能設置了不同的用戶和vhost,也可能運行在不同版本的RabbitMQ和Erang上，Federation基於AMOP 0-9-1協議在不同的Broker之間進行通信。並且設計成能夠密忍不穩定的網路連接情況。

## Federation交換機

### 總體說明

- 各節點操作：啟用聯邦外掛程式
- 下游操作：
  - 添加上游連接端點
  - 創建控制策略

### 準備工作

為了執行相關測試，我們使用Dockert創建兩個RabbitMQ實例。
**特別提示**：由於Federation機制的最大特點就是跨集群同步資料，所以這兩個Docker容器中的RabbitMQ實例不加入集群！！！是兩個**獨立的broker實例**。

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

### 啟用聯邦外掛程式

在上游、下游節點中都需要開啟。
Docker容器中的RabbitMQ已經開啟了rabbitmq_federation,還需要開啟rabbitmq_federation_management

```bash
# 使用以下命令進入 RabbitMQ 容器的 shell
docker exec -it <container_name> /bin/bash

rabbitmq-plugins enable rabbitmq_federation

rabbitmq-plugins enable rabbitmq_federation_management
```

rabbitmq_federation_management外掛程式啟用後會在Management Ul的Admin選項卡下看到：

![image-20241013222423034](assets/670bd81692b37.png)

### 添加上游連接端點

在下游節點填寫上游節點的連接資訊：

```bash
# Name
shiguang.upstream
# URI
amqp://guest:[redacted]@192.168.10.66:51000
```



![image-20241013222715218](assets/670bd8c2cc5b2.png)

### 創建控制策略

![image-20241013222955450](assets/670bd963385ee.png)



![image-20241013222923676](assets/670bd9433b9e8.png)

詳細配置如下：

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

![image-20241013224442084](assets/670bdcd98f54c.png)

### 測試

**① 測試計畫**
**特別提示**：

- 普通交換機和聯邦交換機名稱要一致
- 交換機名稱要能夠和策略規則運算式匹配上
- 發送消息時，兩邊使用的路由鍵也要一致
- 佇列名稱不要求一致

![image-20241013223528412](assets/670bdaafd9145.png)



**② 創建組件**

| 所在機房         | 交換機名稱              | 路由鍵                | 佇列名稱              |
| ---------------- | ----------------------- | --------------------- | --------------------- |
| 深圳機房（上游） | federated.exchange.demo | routing.key.demo.test | queue.normal.shenzhen |
| 上海機房（下游） | federated.exchange.demo | routing.key.demo.test | queue.normal.shanghai |

創建元件後可以查看一下聯邦狀態，連接成功的聯邦狀態如下：

![image-20241013224525828](assets/670bdd054c145.png)

③ 發佈消息執行測試

在上游節點向交換機發佈消息：

![image-20241013224710446](assets/670bdd6df06ef.png)

下游兩個佇列消息總量均變成了1

![image-20241013224807815](assets/670bdda746017.png)



## Federation佇列

### 總體說明

Federation佇列和Federation交換機的最核心區別就是：

- Federation Police作用在交換機上，就是Federation交換機
- Federation Police作用在佇列上，就是Federation佇列

### 創建控制策略

![image-20241013224953436](assets/670bde11003d4.png)

詳細配置如下：

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

![image-20241013225354586](assets/670bdf0212196.png)

### 測試

**① 測試計畫**
上游節點和下游節點中佇列名稱是相同的，只是下游佇列中的節點附加了聯邦策略而已

| 所在機房         | 交換機名稱               | 路由鍵                      | 佇列名稱       |
| ---------------- | ------------------------ | --------------------------- | -------------- |
| 深圳機房（上游） | exchange.normal.shenzhen | routing.key.normal.shenzhen | fed.queue.demo |
| 上海機房（下游） | ——                       | ——                          | fed.queue.demo |

**② 創建組件**
上游節點都是常規操作，此處省略。重點需要關注的是下游節點的聯邦佇列創建時需要指定相關參數：
創建元件後可以查看一下聯邦狀態，連接成功的聯邦狀態如下：

![image-20241013231417003](assets/670be3c863697.png)

**③ 執行測試**
在上游節點向交換機發佈消息：
![image-20241013231549413](assets/670be424d1e9e.png)

但此時發現下游節點中聯邦佇列並沒有接收到消息

![image-20241013231659313](assets/670be46aa74b7.png)

這是為什麼呢？這裡就體現出了聯邦佇列和聯邦交換機工作邏輯的區別。
對聯邦佇列來說，如果沒有監響聯佇列的消費端程式，它是不會到上游去拉取消息的！
如果有消費端監聽聯邦佇列，那麼首先消費聯邦佇列自身的消息；**如果聯邦佇列為空，這時候才會到上游佇列節點中拉取消息。**
所以現在的測試效果需要消費端程式配合才能看到：

![image-20241013232845847](assets/670be72d370db.png)

# Shovel外掛程式

> Shovel 是鏟子的意思，把消息鏟走，從源節點移至目標節點，源節點將收不到消息

## 啟用Shovel外掛程式

```bash
# 使用以下命令進入 RabbitMQ 容器的 shell
docker exec -it <container_name> /bin/bash

rabbitmq-plugins enable rabbitmq_shovel

rabbitmq-plugins enable rabbitmq_shovel_management
```

啟用後管理介面可以看到如下配置：

![image-20241013233431738](assets/670be88711637.png)

## 配置Shovel

> 不區分上下游，在哪個節點配置都可以

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

![image-20241014001059119](assets/670bf11299cce.png)

## 測試

### **測試計畫**

| 所在機房 | 交換機名稱           | 路由鍵               | 佇列名稱                   |
| -------- | -------------------- | -------------------- | -------------------------- |
| 深圳機房 | exchange.shovel.test | exchange.shovel.test | queue.shovel.demo.shenzhen |
| 上海機房 | ——                   | ——                   | queue.shovel.demo.shanghai |

### 測試效果

**① 發佈消息**

![image-20241013234805964](assets/670bebb5718d6.png)



**② 源節點**

![image-20241014003755567](assets/670bf762e144f.png)



**③ 目標節點**


![image-20241014003806164](assets/670bf76d75248.png)


如果測試效果與視頻中演示不一致，可檢查配置的帳號密碼是否正確
可用 `docker logs  <container_name/container_id> 查看日誌`

![image-20241014105658504](assets/670c887a10928.png)

可點擊 Shovels Name 查看配置詳情，例如此處我錯誤地將用戶名寫為`gust`，正確應為 `guest`

![image-20241014110145653](assets/670c8998e97e0.png)

如果帳號密碼配置錯誤導致無法連接，實際測試效果將和普通佇列相同

源節點：
![image-20241013235205538](assets/670beca4dd4ad.png)

目標節點：
![image-20241013235231335](assets/670becbea5201.png)
