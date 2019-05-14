package Rabbitmq.公平转发;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Author: dyh
 * Date:   2019/5/14
 * Description:
 */
public class Worker2 {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 RabbitMQ 的主机名
        factory.setHost("localhost");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个通道
        final Channel channel = connection.createChannel();
        // 指定一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + message + "'");
                try {
                    Thread.sleep(2000);//休息2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 每次处理完成一个消息后，手动发送一次应答。
                    channel.basicAck(envelope.getDeliveryTag(), false);

            }
        };
        //关闭自动应答
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }

//    private static void doWork(String task) throws InterruptedException {
//        String[] taskArr = task.split(":");
//        TimeUnit.SECONDS.sleep(Long.valueOf(taskArr[1]));
//    }

}
