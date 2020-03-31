package com.test.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xionghaiyang
 * @version 1.0
 * @className RabbitProducer
 * @description: TODO
 * @date 1 1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RabbitProducer {

    Logger logger = LoggerFactory.getLogger(RabbitProducer.class);

    @Value("${mq.directExchange.name}")
    private String directExchageName;

    @Value("${mq.fanoutExchange.name}")
    private String fanoutExchangeName;

    @Value("${mq.topicExchange.name}")
    private String topicExchangeName;

    //direct queue 定义
    @Value("${mq.directExchange.queue.name}")
    private String directQueueName;
    @Value("${mq.directExchange.queue.key}")
    private String directQueueKey;

    //fanout queue 定义
    @Value("${mq.fanoutExchange.queue.nameA}")
    private String fanoutQueueNameA;
    @Value("${mq.fanoutExchange.queue.nameB}")
    private String fanoutQueueNameB;
    @Value("${mq.fanoutExchange.queue.nameC}")
    private String fanoutQueueNameC;

    //topic queue 定义
    @Value("${mq.topicExchange.queue.nameA}")
    private String topicQueueNameA;
    @Value("${mq.topicExchange.queue.nameB}")
    private String topicQueueNameB;
    @Value("${mq.topicExchange.queue.nameC}")
    private String topicQueueNameC;


    //消息生产者
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplateM;


    /**
     * direct 交换机发送消息
     */
    @Test
    public void directTest(){
        String msg = " dircet 消息：hello world";
        rabbitTemplate.convertAndSend(directExchageName,directQueueKey,msg);
        logger.info("dircet交换机发送消息：{}", msg);
    }


    @Test
    public void fanoutTest(){
        String msg = " fanout 消息：hello world";
        rabbitTemplate.convertAndSend(fanoutExchangeName,"xxx",msg+"1");
        rabbitTemplate.convertAndSend(fanoutExchangeName,null,msg+"2");
        logger.info("fanout交换机发送消息：{}", msg);
    }

    @Test
    public void topicTest1(){
        String msg = " topic 消息：hello world";
        rabbitTemplate.convertAndSend(topicExchangeName,"TestTopic.xhy",msg);
        logger.info("topic交换机向 TestTopic.xhy 发送消息：{}", msg);
    }
    @Test
    public void topicTest2(){
        String msg = " topic 消息：hello world";
        rabbitTemplate.convertAndSend(topicExchangeName,"TestTopic.xxxx",msg);
        logger.info("topic交换机向 TestTopic.xxx 发送消息：{}", msg);
    }
    @Test
    public void topicTest3(){
        String msg = " topic 消息：hello world";
        rabbitTemplate.convertAndSend(topicExchangeName,"TestTopic.demo.test",msg);
        logger.info("topic交换机向 TestTopic.demotest 发送消息：{}", msg);
    }


    //生产者发送消息确认

    /**
     * 找不到交换机
     */
    @Test
    public void directMsgTest1(){
        String msg = " dircet 消息确认：hello world";
        rabbitTemplateM.convertAndSend("not-exchange",directQueueKey,msg);
        logger.info("dircet交换机发送消息需要确认：{}", msg);
    }

    /**
     * 找不到队列
     */
    @Test
    public void directMsgTest2(){
        String msg = " dircet 消息确认：hello world";
        rabbitTemplateM.convertAndSend(directExchageName,"not-queue",msg);
        logger.info("dircet交换机发送消息需要确认：{}", msg);
    }

    /**
     * 交换机、队列都不存在
     */
    @Test
    public void directMsgTest3(){
        String msg = " dircet 消息确认：hello world";
        rabbitTemplateM.convertAndSend("not-exchage","not-queue",msg);
        logger.info("dircet交换机发送消息需要确认：{}", msg);
    }

    /**
     * 正常情况
     */
    @Test
    public void directMsgTest4(){
        String msg = " dircet 消息确认：hello world";
        rabbitTemplateM.convertAndSend(directExchageName,directQueueKey,msg);
        logger.info("dircet交换机发送消息需要确认：{}", msg);
    }


    /**
     * 接收者消息回调
     */
    @Test
    public void msgCallBack(){
        String msg = " dircet 消息确认：hello world";
        rabbitTemplateM.convertAndSend("callback-exchange","callbak-key",msg);
        logger.info("dircet交换机发送消息需要确认：{}", msg);
    }


    //延迟消息处理
    @Test
    public void msgProcess() throws Exception{
        String msg = " hello world";
        MessagePostProcessor postProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setContentEncoding("utf-8");
                //单位毫秒
                message.getMessageProperties().setExpiration(String.valueOf(1000*30));
                return message;
            }
        };
        rabbitTemplateM.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_NAME,"ttl.queue.test",msg,postProcessor);
        logger.info("延迟消息发送时间：{}", new Date());

        //为了30秒过期演示，此处休眠60秒
        Thread.sleep(60*1000);
    }

    //事务消息处理
    @Autowired
    RabbitTemplate rabbitTemplateT;

    @Test
    @Transactional(rollbackFor = Exception.class, transactionManager = "rabbitTransactionManager")
    public void txmsgProcess() throws Exception{
        String msg = " hello world";
        rabbitTemplateT.convertAndSend("tx-exchange","tx-queue",msg);
        int x = 2/0;
    }



}
