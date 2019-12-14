package com.sxq.practice.mq.kafka.rebalance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.kafka.ConsumerRecordProcessor;
import com.sxq.practice.mq.kafka.KafkaConstants;
import com.sxq.practice.mq.kafka.KafkaUtil;

/**
 * Created by s-xq on 2019-12-12.
 */

public class HandleRebalanceConsumer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    private static AtomicLong instanceNum = new AtomicLong(0);

    private Consumer<String, String> consumer;

    public HandleRebalanceConsumer() {
        super(String.format("HandleRebalanceConsumer-%d", instanceNum.incrementAndGet()));
    }

    @Override
    public void run() {
        launchConsumer();
    }

    public static void main(String[] args) throws InterruptedException {

        List<HandleRebalanceConsumer> consumers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HandleRebalanceConsumer handleRebalanceConsumer = new HandleRebalanceConsumer();
            consumers.add(handleRebalanceConsumer);
            handleRebalanceConsumer.start();
        }
        for (HandleRebalanceConsumer consumer : consumers) {
            try {
                Thread.sleep(15000);
                /**
                 *  kill consumer thread by wakeup consumer
                 */
                consumer.getConsumer().wakeup();
                consumer.join();
            } catch (InterruptedException ex) {
                logger.info("consumer[{}] closed", consumer.getName());
            }
        }
    }

    public Consumer<String, String> getConsumer() {
        return consumer;
    }

    private void launchConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("group.id", KafkaUtil.consumerGroupName(KafkaConstants.ExampleModule.MODULE_HANDLE_REBALANCE));
        properties.put("key.deserializer", StringDeserializer.class.getCanonicalName());
        properties.put("value.deserializer", StringDeserializer.class.getCanonicalName());
        consumer = new KafkaConsumer(properties);
        Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
        consumer.subscribe(Arrays.asList(
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_SIMPLE),
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_IDEMPOTENCE),
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_TRANSACTIONAL)),
                new HandleRebalance(currentOffset, consumer));
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord consumerRecord : consumerRecords) {
                    new ConsumerRecordProcessor(consumerRecord).process();
                    currentOffset.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                            new OffsetAndMetadata(consumerRecord.offset() + 1, "no metadata"));
                }
                consumer.commitAsync(currentOffset, null);
            }
        } catch (WakeupException ex) {
            logger.info("consumer wakeup, closing");
        } catch (Exception ex) {
            //            logger.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } catch (Exception ex) {
                //                logger.error(ExceptionUtils.getStackTrace(ex));
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
}
