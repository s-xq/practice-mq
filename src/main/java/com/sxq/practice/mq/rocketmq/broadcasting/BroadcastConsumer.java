package com.sxq.practice.mq.rocketmq.broadcasting;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.rocketmq.MqUtil;
import com.sxq.practice.mq.rocketmq.RocketMQConstants;

/**
 * Created by s-xq on 2019-12-10.
 */

public class BroadcastConsumer {

    private static Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer =
                new DefaultMQPushConsumer(MqUtil.consumerGroupName(RocketMQConstants.ExampleModule.MODULE_BROADCAST));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        String[] tags = MqUtil.multiTagsName(RocketMQConstants.ExampleModule.MODULE_BROADCAST);
        consumer.subscribe(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_BROADCAST),
                StringUtils.join(" | ", tags));
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                logger.info("Receive new message:{}", msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        logger.info("Broadcast Consumer Started.");

    }

}
