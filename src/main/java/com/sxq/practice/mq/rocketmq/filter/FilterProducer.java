package com.sxq.practice.mq.rocketmq.filter;

import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.rocketmq.MqUtil;
import com.sxq.practice.mq.rocketmq.RocketMQConstants;

/**
 * Created by s-xq on 2019-12-10.
 */

public class FilterProducer {
    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args)
            throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException,
            MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer(
                MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_FILTER));
        producer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        producer.start();
        for (int i = 0; i < 100; i++) {
            Message message = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_FILTER),
                    MqUtil.tagName(RocketMQConstants.ExampleModule.MODULE_FILTER),
                    ("Hello world " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            message.putUserProperty("a", String.valueOf(i));
            SendResult sendResult = producer.send(message);
            logger.info("SendResult:[{}] {}", i, sendResult);
        }
        producer.shutdown();
    }
}
