package com.sxq.practice.mq.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-12.
 */

public class ManualOffsetControlConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("group.id",
                KafkaUtil.consumerGroupName(KafkaConstants.ExampleModule.MODULE_MANUAL_OFFSET_CONTROL));
        properties.put("enable.auto.commit", false);
        properties.put("auto.commit.interval.ms", 1000);
        properties.put("key.serializer", StringSerializer.class.getCanonicalName());
        properties.put("value.serializer", StringSerializer.class.getCanonicalName());
        Consumer consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_SIMPLE),
                KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_IDEMPOTENCE)));
        final int miniBatchSize = 200;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(200));
            for (ConsumerRecord consumerRecord : consumerRecords) {
                buffer.add(consumerRecord);
            }
            if (buffer.size() > miniBatchSize) {
                logger.info("handle buffer:[{}]", buffer);
                consumer.commitSync();
                buffer.clear();
            }
        }

    }
}
