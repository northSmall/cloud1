package com.north.study.controller;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 消费者Controller
 * @Title ConsumerController
 * @author LinkedBear
 * @Time 2018年8月2日 下午3:22:11
 */
@Controller
public class ConsumerController {
    @RequestMapping("/getMessage")
    @ResponseBody
    public void getMessage() throws Exception {
        //1. 创建消费者连接，要传入MQ的分组名，该分组名在ProducerController中
        //此处创建的是pushConsumer，它使用监听器，给人的感觉是消息被推送的
        //pullConsumer，取消息的过程需要自己写      
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(ProducerController.PRODUCE_GROUP_NAME);
        //2. 设置MQ的运行地址
        consumer.setNamesrvAddr(ProducerController.MQ_IP);
        //3. 设置消息的提取顺序
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //4. 设置消费者接收消息的Topic和Tag，此处对Tag不作限制
        consumer.subscribe("test_topic", "*");
        
        //5. 使用监听器接收消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {
                try {
                    for (MessageExt messageExt : msgs) {
                        String message = new String(messageExt.getBody(), "utf-8");
                        System.out.println("收到消息【主题：" + messageExt.getTopic() + ", 正文：" + message + "】");
                    }
                    
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    //转换出现问题，稍后重新发送
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        
        //6. 启动消费者
        consumer.start();
    }
}
