package com.test.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xionghaiyang
 * @version A.0
 * @className RabbitMQConfig
 */
@Configuration
public class RabbitMQConfig {

    Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

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



    //headers exchange不做示例

    //Direct Exchange
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(directExchageName);
    }

    @Bean
    public Queue directQueue(){
        return new Queue(directQueueName,true);
    }

    @Bean
    public Binding bindingDirect(){
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(directQueueKey);
    }

    //Fanout  Exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(fanoutExchangeName);
    }

    @Bean
    public Queue fanoutQueueA(){
        return new Queue(fanoutQueueNameA);
    }

    @Bean
    public Queue fanoutQueueB(){
        return new Queue(fanoutQueueNameB);
    }

    @Bean
    public Queue fanoutQueueC(){
        return new Queue(fanoutQueueNameC);
    }

    @Bean
    public Binding bindingFanoutA(){
        return BindingBuilder.bind(fanoutQueueA()).to(fanoutExchange());
    }
    @Bean
    public Binding bindingFanoutB(){
        return BindingBuilder.bind(fanoutQueueB()).to(fanoutExchange());
    }
    @Bean
    public Binding bindingFanoutC(){
        return BindingBuilder.bind(fanoutQueueC()).to(fanoutExchange());
    }


    //Topic Exchange
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public Queue topicQueueA(){
        return new Queue(topicQueueNameA);
    }

    @Bean
    public Queue topicQueueB(){
        return new Queue(topicQueueNameB);
    }

    @Bean
    public Queue topicQueueC(){
        return new Queue(topicQueueNameC);
    }

    @Bean
    public Binding bindingTopicA(){
        return BindingBuilder.bind(topicQueueA()).to(topicExchange()).with("TestTopic.xhy");
    }
    @Bean
    public Binding bindingTopicB(){
        return BindingBuilder.bind(topicQueueB()).to(topicExchange()).with("TestTopic.#");
    }
    @Bean
    public Binding bindingTopicC(){
        return BindingBuilder.bind(topicQueueC()).to(topicExchange()).with("TestTopic.*.test");
    }



    //创建RabbitTemplate
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplateM")
    public RabbitTemplate rabbitTemplateM(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        //确认交换机回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                logger.info("ConfirmCallback >>>>> ");
                logger.info("correlationData: {}", correlationData);
                logger.info("ack: {}", ack);
                logger.info("cause: {}",cause);
            }
        });

        //确认队列回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("ReturnCallback >>>>> ");
                logger.info("message: {}", message);
                logger.info("replyCode: {}", replyCode);
                logger.info("replyText: {}", replyText);
                logger.info("exchange: {}", exchange);
                logger.info("routingKey: {}", routingKey);
            }
        });


        return rabbitTemplate;
    }




    @Bean
    public DirectExchange exchange(){
        return new DirectExchange("callback-exchange");
    }

    @Bean
    public Queue callbackQueue(){
        return new Queue("callback-queue-test",true);
    }

    @Bean
    public Binding bindingCallback(){
        return BindingBuilder.bind(callbackQueue()).to(exchange()).with("callbak-key");
    }



    //延迟消息处理
    /**
     * 普通消息通知队列名称
     */
    public static final String MESSAGE_QUEUE_NAME="message.ordinary.queue";

    /**
     * 普通交换机名称
     */

    public static final String DIRECT_EXCHANGE_NAME="message.ordinary.exchange";

    /**
     * ttl(延时)消息通知队列名称
     */
    public static final String MESSAGE_TTL_QUEUE_NAME="message.ttl.queue";



    /**
     * ttl(延时)交换机名称
     */
    public static final String TOPIC_EXCHANGE_NAME="message.ttl.exchange";



    /**
     * 普通消息交换机配置
     *
     * @return
     */
    @Bean
    public DirectExchange messageDirect() {
        return new DirectExchange(RabbitMQConfig.DIRECT_EXCHANGE_NAME) ;
    }
    /**
     * 普通消息队列配置
     *
     * @return
     */
    @Bean
    public Queue messageQueue() {
        return new Queue(RabbitMQConfig.MESSAGE_QUEUE_NAME);
    }


    /**
     * 延时消息交换机配置
     *
     * @return
     */
    @Bean
    public TopicExchange messageTtlDirect() {
        return new TopicExchange(RabbitMQConfig.TOPIC_EXCHANGE_NAME) ;
    }


    /**
     * TTL消息队列配置
     *
     * @return
     */
    @Bean
    public Queue messageTtlQueue() {
        return QueueBuilder
                .durable(RabbitMQConfig.MESSAGE_TTL_QUEUE_NAME)
                // 配置到期后转发的交换
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DIRECT_EXCHANGE_NAME)
                // 配置到期后转发的路由键
                .withArgument("x-dead-letter-routing-key", "dead.queue.test")
                .build();
    }

    /**
     * 普通队列和普通交换机的绑定-routekey
     */
    @Bean
    Binding messageBinding() {
        return BindingBuilder
                .bind(messageQueue())
                .to(messageDirect())
                .with("dead.queue.test");
    }

    /**
     * ttl队列和ttl交换机的绑定-routekey
     */
    @Bean
    public Binding messageTtlBinding() {
        return BindingBuilder
                .bind(messageTtlQueue())
                .to(messageTtlDirect())
                .with("ttl.queue.test");
    }


    //=================消息事务=================

    @Bean("rabbitTransactionManager")
    public RabbitTransactionManager rabbitTransactionManager(CachingConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean(name = "rabbitTemplateT")
    public RabbitTemplate rabbitTemplateT(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setChannelTransacted(true);

        /*//确认交换机回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                logger.info("ConfirmCallback >>>>> ");
                logger.info("correlationData: {}", correlationData);
                logger.info("ack: {}", ack);
                logger.info("cause: {}",cause);
            }
        });*/

        //确认队列回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("ReturnCallback >>>>> ");
                logger.info("message: {}", message);
                logger.info("replyCode: {}", replyCode);
                logger.info("replyText: {}", replyText);
                logger.info("exchange: {}", exchange);
                logger.info("routingKey: {}", routingKey);
            }
        });



        return rabbitTemplate;
    }

    @Value("${mq.tx.exchange}")
    private String exchange;

    @Value("${mq.tx.queue}")
    private String queue;

    @Bean
    public DirectExchange txExchange() {
        return new DirectExchange(exchange,true,false);
    }

    @Bean
    public Queue ingateQueue() {
        return new Queue(queue,true);
    }

    @Bean
    public Binding ingateQueueBinding() {
        return BindingBuilder.bind(ingateQueue()).to(txExchange()).withQueueName();
    }


}
