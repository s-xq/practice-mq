package com.sxq.practice.mq.kafka.stream;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.kafka.KafkaConstants;
import com.sxq.practice.mq.kafka.KafkaUtil;

/**
 * Created by s-xq on 2019-12-12.
 */

public class WordCountConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("group.id", KafkaUtil.consumerGroupName(KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT));
        properties.put("enable.auto.commit", true);
        properties.put("auto.commit.interval.ms", 1000);
        properties.put("key.deserializer", StringDeserializer.class.getCanonicalName());
        properties.put("value.deserializer", LongDeserializer.class.getCanonicalName());
        Consumer<String, String> consumer = new KafkaConsumer(properties);
        consumer.subscribe(Arrays.asList(
                KafkaUtil.streamOutputTopicName(KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT)));
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
        }
    }

}
