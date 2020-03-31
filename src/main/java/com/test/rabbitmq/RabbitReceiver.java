package com.test.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author xionghaiyang
 * @version 1.0
 * @className RabbitReceiver
 * @description: TODO
 * @date 1 1
 */

@Component
public class RabbitReceiver {

    Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);

    public final String directQueueName = "direct-queue-test";
    public final String fanoutQueueNameA = "fanout-queue-test1";
    public final String fanoutQueueNameB = "fanout-queue-test2";
    public final String fanoutQueueNameC = "fanout-queue-test3";
    public final String topicQueueNameA = "topic-queue-test1";
    public final String topicQueueNameB = "topic-queue-test2";
    public final String topicQueueNameC = "topic-queue-test3";

    /**
     * direct 交换机接收队列消息
     * @param msg
     */
    @RabbitListener(queues = {directQueueName} )
    @RabbitHandler
    public void directProcess(String msg){
        logger.info("dircet交换机接收到消息：{}",msg);
    }


    @RabbitListener(queues = {fanoutQueueNameA} )
    @RabbitHandler
    public void fanoutProcessA(String msg){
        logger.info("fanout：[{}]，接收到消息：{}",fanoutQueueNameA,msg);
    }

    @RabbitListener(queues = {fanoutQueueNameB} )
    @RabbitHandler
    public void fanoutProcessB(String msg){
        logger.info("fanout：[{}]，接收到消息：{}",fanoutQueueNameB,msg);
    }


    @RabbitListener(queues = {fanoutQueueNameC} )
    @RabbitHandler
    public void fanoutProcessC(String msg){
        logger.info("fanout：[{}]，接收到消息：{}",fanoutQueueNameC,msg);
    }


    @RabbitListener(queues = {topicQueueNameA} )
    @RabbitHandler
    public void topicProcessA(String msg){
        logger.info("topic：[{}]，接收到消息：{}",topicQueueNameA,msg);
    }

    @RabbitListener(queues = {topicQueueNameB} )
    @RabbitHandler
    public void topicProcessB(String msg){
        logger.info("topic：[{}]，接收到消息：{}",topicQueueNameB,msg);
    }

    @RabbitListener(queues = {topicQueueNameC} )
    @RabbitHandler
    public void topicProcessC(String msg){
        logger.info("topic：[{}]，接收到消息：{}",topicQueueNameC,msg);
    }



    @RabbitListener(queues = {"callback-queue-test"} )
    @RabbitHandler
    public void directMsgProcess(Message message, Channel channel) throws Exception{
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //message即接收的消息
            logger.info("接收消息手动确认：{}",message.toString());
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            logger.error("接收消息手动确认出现异常：", e);
            channel.basicReject(deliveryTag,false);
        }
    }


    /**
     * 延迟消息接收
     */
    @RabbitListener(queues = {RabbitMQConfig.MESSAGE_QUEUE_NAME} )
    @RabbitHandler
    public void deadMsg(Message message, Channel channel) throws Exception{
        logger.info("接收到消息时间：{}",new Date());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //message即接收的消息
            logger.info("接收消息手动确认：{}",message.toString());
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            logger.error("接收消息手动确认出现异常：", e);
            channel.basicReject(deliveryTag,false);
        }
    }


    /**
     * 消息事务
     */
    @RabbitListener(queues = {"tx-queue"} )
    @RabbitHandler
    public void txMsg(String msg) throws Exception{
        logger.info("接收事务消息：{}",msg);
    }

}
