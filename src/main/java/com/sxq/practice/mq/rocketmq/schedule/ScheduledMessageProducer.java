package com.sxq.practice.mq.rocketmq.schedule;

import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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

public class ScheduledMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args)
            throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException,
            MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer(
                MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_SCHEDULE));
        producer.start();
        int totalMessageToSend = 100;
        for (int i = 0; i < totalMessageToSend; i++) {
            Message message = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_SCHEDULE),
                    ("Hello scheduled message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            /**
             * This message will be delivered to consumer 10 seconds later
             */
            message.setDelayTimeLevel(3);
            producer.send(message);
        }
        producer.shutdown();
    }
}
