package com.sxq.practice.mq.rocketmq.order;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
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

public class OrderedProducer {

    private static Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException,
            UnsupportedEncodingException {
        DefaultMQProducer mqProducer = new DefaultMQProducer(
                MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_ORDER));
        mqProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        mqProducer.start();
        String[] tags = MqUtil.multiTagsName(RocketMQConstants.ExampleModule.MODULE_ORDER);
        for (int i = 0; i < 100; i++) {
            int orderId = i % 10;
            Message msg = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_ORDER),
                    tags[i % tags.length],
                    "KEY" + i,
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = mqProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);
            logger.info("SendResult:{}", sendResult);
        }
        mqProducer.shutdown();
    }
}
