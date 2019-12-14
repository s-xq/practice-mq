package com.sxq.practice.mq.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-12.
 */

public class HandleRebalanceUsingDbTransactionConsumer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    private static AtomicLong instanceNum = new AtomicLong(0);

    public HandleRebalanceUsingDbTransactionConsumer() {
        super(String.format("HandleRebalanceConsumer-%d", instanceNum.incrementAndGet()));
    }

    @Override
    public void run() {
        launchConsumer();
    }

    public static void main(String[] args) throws InterruptedException {
        List<HandleRebalanceUsingDbTransactionConsumer> consumers =
                Arrays.asList(new HandleRebalanceUsingDbTransactionConsumer(),
                        new HandleRebalanceUsingDbTransactionConsumer(),
                        new HandleRebalanceUsingDbTransactionConsumer());
        for (HandleRebalanceUsingDbTransactionConsumer consumer : consumers) {
            consumer.start();
        }
        for (HandleRebalanceUsingDbTransactionConsumer consumer : consumers) {
            try {
                Thread.sleep(15000);
                consumer.interrupt();
            } catch (InterruptedException ex) {
                logger.info("consumer[{}] closed", consumer.getName());
            }
        }
    }

    private void launchConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("group.id", KafkaUtil.consumerGroupName(KafkaConstants.ExampleModule.MODULE_HANDLE_REBALANCE));
        properties.put("key.deserializer", StringDeserializer.class.getCanonicalName());
        properties.put("value.deserializer", StringDeserializer.class.getCanonicalName());
        Consumer<String, String> consumer = new KafkaConsumer(properties);
        HandleRebalanceUsingDbTransaction handleRebalanceUsingDbTransaction =
                new HandleRebalanceUsingDbTransaction(consumer);
        consumer.subscribe(Arrays.asList(
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_SIMPLE),
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_IDEMPOTENCE),
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_TRANSACTIONAL)),
                handleRebalanceUsingDbTransaction);
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord consumerRecord : consumerRecords) {
                    new ConsumerRecordProcessor(consumerRecord).process();
                    DbModule.storeRecordInDb(consumerRecord);
                    DbModule.storeOffsetInDb(consumerRecord.topic(),
                            consumerRecord.partition(),
                            consumerRecord.offset());
                }
                DbModule.commitDbTransaction();
            }
        } catch (WakeupException ex) {
            logger.error("consumer closing");
        } catch (Exception ex) {
            //            logger.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                consumer.close();
            } catch (Exception ex) {
                //                logger.error(ExceptionUtils.getStackTrace(ex));
            }
            logger.info("consumer closed");
        }
    }

}
