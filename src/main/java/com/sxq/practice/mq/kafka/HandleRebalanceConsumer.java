package com.sxq.practice.mq.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

/**
 * Created by s-xq on 2019-12-12.
 */

public class HandleRebalanceConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("group.id", KafkaUtil.consumerGroupName(KafkaConstants.ExampleModule.MODULE_HANDLE_REBALANCE));
        properties.put("key.deserializer", StringDeserializer.class.getCanonicalName());
        properties.put("value.deserializer", StringDeserializer.class.getCanonicalName());
        Consumer<String, String> consumer = new KafkaConsumer(properties);
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
                    logger.info("topic:[{}], \tpartition:[{}], \toffset:[{}], \tkey:[{}], \tvalue:[{}]",
                            consumerRecord.topic(),
                            consumerRecord.partition(),
                            consumerRecord.offset(),
                            consumerRecord.key(),
                            consumerRecord.value());
                }
                consumer.commitAsync(currentOffset, null);
            }
        } catch (WakeupException ex) {
            logger.error("consumer closing");
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } catch (Exception ex) {
                logger.error(ExceptionUtils.getStackTrace(ex));
            } finally {
                consumer.close();
                logger.info("consumer closed");
            }
        }
    }
}