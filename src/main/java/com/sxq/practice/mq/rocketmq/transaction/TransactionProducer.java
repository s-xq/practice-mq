package com.sxq.practice.mq.rocketmq.transaction;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
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

public class TransactionProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.ROCKET_MQ);

    public static void main(String[] args) throws InterruptedException, MQClientException {
        TransactionListener transactionListener = new TransactionListenerImpl();
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer(MqUtil.producerGroupName(
                RocketMQConstants.ExampleModule.MODULE_TRANSACTION));
        transactionMQProducer.setNamesrvAddr(RocketMQConstants.NAME_SRV_ADDR);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        transactionMQProducer.setExecutorService(executorService);
        transactionMQProducer.setTransactionListener(transactionListener);
        transactionMQProducer.start();
        String[] tags = MqUtil.multiTagsName(RocketMQConstants.ExampleModule.MODULE_TRANSACTION);
        for (int i = 0; i < 10; i++) {
            try {
                Message msg = new Message(MqUtil.topicName(RocketMQConstants.ExampleModule.MODULE_TRANSACTION),
                        tags[i % tags.length], ("KEY" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = transactionMQProducer.sendMessageInTransaction(msg, null);
                logger.info("SendResult:[{}]\t[{}]", i, sendResult);
                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        transactionMQProducer.shutdown();
    }
}
