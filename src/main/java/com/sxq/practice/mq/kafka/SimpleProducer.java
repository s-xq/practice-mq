package com.sxq.practice.mq.kafka;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-12.
 */

public class SimpleProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("acks", "all");
        properties.put("key.serializer", StringSerializer.class.getCanonicalName());
        properties.put("value.serializer", StringSerializer.class.getCanonicalName());
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i = 0; i < 10000; i++) {
            String key = System.currentTimeMillis() + "-" + i;
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                    KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_SIMPLE),
                    key,
                    key + "-value");
            producer.send(producerRecord);
            logger.info("send [{}] message to topic[{}], key:[{}], value:[{}]",
                    i, producerRecord.topic(), producerRecord.key(), producerRecord.value());
            try {
                Thread.sleep(1000);
            } catch (Throwable throwable) {
                logger.info(ExceptionUtils.getStackTrace(throwable));
            }
        }
        producer.close();
    }
}
