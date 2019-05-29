package Rabbitmq.死信队列;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * Author: dyh
 * Date:   2019/4/26
 * Description:
 * 生产者首先发送一条携带路由键为 " rk " 的消息，然后经过交换器
 exchange .normal 顺利地存储到队列 queue.normal 中 。由于队列 queue.normal 设置了过期时间为
 10s ， 在这 10s 内没有消费者消费这条消息，那么判定这条消息为过期。由于设置了 DLX ， 过期
 之时 ， 消息被丢给交换器 exchange.dlx 中，这时找到与 exchange.dlx 匹配的队列 queue .dlx ， 最
 后消息被存储在 queue.dlx 这个死信队列中。
 */
public class Send
{


    public static void main(String[] args) throws IOException,TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange.dlx","direct",true);
        channel.exchangeDeclare("exchange.normal","fanout",true);
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","dlx_exchange");
        map.put("x-dead-letter-routing-key","routingkey");
        map.put("x-message-ttl",10000);
        channel.queueDeclare("queue.normal", true, false, false, map);
        channel.queueBind("queue.normal","exchange.normal","");
        channel.queueDeclare("queue.dlx", true, false, false, null);
        channel.queueBind("queue.dlx","exchange.dlx","routingkey");

        channel.basicPublish("exchange.normal", "rk", MessageProperties.PERSISTENT_TEXT_PLAIN, "dlx".getBytes());

        channel.close();
        connection.close();
    }
}
