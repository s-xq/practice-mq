package com.sxq.practice.mq.kafka.stream;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.kafka.KafkaConstants;
import com.sxq.practice.mq.kafka.KafkaUtil;

/**
 * Created by s-xq on 2019-12-12.
 */

public class WordCountProducer {

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
                    KafkaUtil.streamInputTopicName(KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT),
                    key,
                    key + "-value");
            producer.send(producerRecord);
            logger.info("topic:[{}], \tpartition:[{}], \tkey:[{}], \tvalue:[{}]",
                    producerRecord.topic(),
                    producerRecord.partition(),
                    producerRecord.key(),
                    producerRecord.value());
            try {
                Thread.sleep(1000);
            } catch (Throwable throwable) {
                logger.info(ExceptionUtils.getStackTrace(throwable));
            }
        }
        producer.close();
    }
}
