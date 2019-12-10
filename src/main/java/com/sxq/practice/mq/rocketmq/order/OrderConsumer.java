package com.sxq.practice.mq.rocketmq.order;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.rocketmq.MqUtil;
import com.sxq.practice.mq.rocketmq.RocketMQConstants;

/**
 * Created by s-xq on 2019-12-10.
 */

public class OrderConsumer {

    private static Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
                MqUtil.consumerGroupName(RocketMQConstants.ExampleModule.MODULE_ORDER));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        String[] tags = MqUtil.multiTagsName(RocketMQConstants.ExampleModule.MODULE_ORDER);
        consumer.subscribe(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_ORDER),
                StringUtils.joinWith(" | ", tags));
        consumer.registerMessageListener(new MessageListenerOrderly() {
            private AtomicLong consumeTime = new AtomicLong(0);

            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(false);
                logger.info("Receive new message:{}", msgs);
                this.consumeTime.incrementAndGet();
                if ((this.consumeTime.get() % 2) == 0) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else if ((this.consumeTime.get() % 3 == 0)) {
                    return ConsumeOrderlyStatus.ROLLBACK;
                } else if (this.consumeTime.get() % 4 == 0) {
                    return ConsumeOrderlyStatus.COMMIT;
                } else if (this.consumeTime.get() % 5 == 0) {
                    context.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        logger.info("Consumer Started");
    }
}
