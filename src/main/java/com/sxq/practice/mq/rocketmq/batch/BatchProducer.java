package com.sxq.practice.mq.rocketmq.batch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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

public class BatchProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws UnsupportedEncodingException, MQClientException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(
                MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_BATCH));
        defaultMQProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        defaultMQProducer.start();
        List<Message> messageList = new ArrayList<>();
        String topic = MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_BATCH);
        for (int i = 0; i < 100; i++) {
            messageList.add(new Message(topic, MqUtil.tagName(RocketMQConstants.ExampleModule.MODULE_BATCH),
                    ("Hello world " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)));
        }
        ListSplitter splitter = new ListSplitter(messageList);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                defaultMQProducer.send(listItem);
            } catch (Exception ex) {
                logger.error(ExceptionUtils.getStackTrace(ex));
            }
        }
        defaultMQProducer.shutdown();
    }
}
