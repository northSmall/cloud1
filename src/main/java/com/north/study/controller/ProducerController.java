package com.north.study.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 生产者Controller
 * @Title ProducerController
 * @author LinkedBear
 * @Time 2018年8月2日 下午3:22:02
 */
@Controller
public class ProducerController {
    //此分组名必须保证全局唯一(考虑到负载均衡等后续问题)，故封装为静态常量
    public static final String PRODUCE_GROUP_NAME = "TestGroup";
    //MQ的运行地址
    public static final String MQ_IP = "127.0.0.1:9876";
    
    @RequestMapping("/produceMessage")
    @ResponseBody
    public Map<String, Object> produceMessage() throws Exception {
        //1. 创建生产者连接（类似于JDBC中的Connection），要传入MQ的分组名
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCE_GROUP_NAME);
        //2. 设置MQ的运行地址
        producer.setNamesrvAddr(MQ_IP);
        //3. 开启连接
        producer.start();
        
        //4. 构造消息(重载方法较多，此处选择topic, tag, message的三参数方法)
        Message message = new Message("test_topic", "test_tag", ("test_message。。。" + Math.random()).getBytes());
        //5. 发送消息，该方法会返回一个发送结果的对象
        SendResult result = producer.send(message);
        System.out.println(result.getSendStatus());
        //6. 关闭连接
        producer.shutdown();
        
        //此处将发送结果显示在页面上，方便查看
        Map<String, Object> map = new HashMap<>();
        map.put("消息", result.getSendStatus());
        return map;
    } 
}
