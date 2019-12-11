package com.sxq.practice.mq.rocketmq.filter;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
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

public class FilterConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(
                MqUtil.consumerGroupName(RocketMQConstants.ExampleModule.MODULE_FILTER));
        defaultMQPushConsumer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        /**
         * must update broker config:
         * ./mqadmin updateBrokerConfig -n localhost:9876 -b 172.24.175.60:10911 -k enablePropertyFilter -v true
         */
        defaultMQPushConsumer.subscribe(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_FILTER),
                MessageSelector.bySql("a between 0 and 3"));
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                logger.info("Receive new message:{}", msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumer.start();
    }

}
