package com.sxq.practice.mq.rocketmq.schedule;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.rocketmq.MqUtil;
import com.sxq.practice.mq.rocketmq.RocketMQConstants;

/**
 * Created by s-xq on 2019-12-10.
 */

public class ScheduledMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
                MqUtil.consumerGroupName(RocketMQConstants.ExampleModule.MODULE_SCHEDULE));
        consumer.subscribe(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_SCHEDULE), "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt messageExt : msgs) {
                    logger.info("Receive new message [msgId={}], {} ms later", messageExt.getMsgId(),
                            System.currentTimeMillis() - messageExt.getStoreTimestamp());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
