package com.sxq.practice.mq.rocketmq.simple;

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
 * Created by s-xq on 2019-12-09.
 */
public class SyncProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args)
            throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException,
            MQBrokerException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(
                MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_SIMPLE));
        defaultMQProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        defaultMQProducer.start();
        for (int i = 0; i < 100; i++) {
            Message msg = new Message(
                    MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_SIMPLE),
                    MqUtil.tagName(RocketMQConstants.ExampleModule.MODULE_SIMPLE),
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = defaultMQProducer.send(msg);
            logger.info("SendResult:{}-{}", i, sendResult);
        }
        defaultMQProducer.shutdown();
    }
}
