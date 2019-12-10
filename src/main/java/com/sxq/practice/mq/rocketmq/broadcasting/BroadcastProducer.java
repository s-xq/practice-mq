package com.sxq.practice.mq.rocketmq.broadcasting;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.rocketmq.MqUtil;
import com.sxq.practice.mq.rocketmq.RocketMQConstants;

/**
 * Created by s-xq on 2019-12-10.
 */

public class BroadcastProducer {

    private static Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws Exception {
        DefaultMQProducer defaultMQProducer =
                new DefaultMQProducer(MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_BROADCAST));
        defaultMQProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        defaultMQProducer.start();
        for (int i = 0; i < 100; i++) {
            Message msg = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_BROADCAST),
                    MqUtil.tagName(RocketMQConstants.ExampleModule.MODULE_BROADCAST),
                    MqUtil.keysName(RocketMQConstants.ExampleModule.MODULE_BROADCAST),
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = defaultMQProducer.send(msg);
            logger.info("SendResult:{}", sendResult);
        }
        defaultMQProducer.shutdown();
    }
}
