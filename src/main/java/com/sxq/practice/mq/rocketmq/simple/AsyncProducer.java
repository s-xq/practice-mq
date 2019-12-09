package com.sxq.practice.mq.rocketmq.simple;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
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

public class AsyncProducer {

    private static Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args)
            throws RemotingException, MQClientException, InterruptedException, UnsupportedEncodingException {
        DefaultMQProducer defaultMQProducer
                = new DefaultMQProducer(MqUtil.producerGroupName(RocketMQConstants.ExampleModule.MODULE_SIMPLE));
        defaultMQProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        defaultMQProducer.start();
        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(0);
        for (int i = 0; i < 100; i++) {
            final int index = i;
            Message msg = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_SIMPLE),
                    MqUtil.tagName(RocketMQConstants.ExampleModule.MODULE_SIMPLE),
                    MqUtil.keysName(RocketMQConstants.ExampleModule.MODULE_SIMPLE),
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            defaultMQProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.info("Send {} ID[{}] success", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("Send {} exception:{}", index, ExceptionUtils.getStackTrace(e));
                }
            });
        }
        defaultMQProducer.shutdown();
    }
}
