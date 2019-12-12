package com.sxq.practice.mq.kafka;

import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-12.
 */

public class TransactionalProducer {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KafkaConstants.BOOTSTRAP_SERVERS);
        properties.put("transactional.id", "my-transactional-id");
        properties.put("key.serializer", StringSerializer.class.getCanonicalName());
        properties.put("value.serializer", StringSerializer.class.getCanonicalName());
        Producer producer = new KafkaProducer<String, String>(properties);
        producer.initTransactions();
        try {
            producer.beginTransaction();
            for (int i = 0; i < 100; i++) {
                producer.send(new ProducerRecord(
                        KafkaUtil.topicName(KafkaConstants.ExampleModule.MODULE_TRANSACTIONAL),
                        Integer.toString(i),
                        Integer.toString(i)));
            }
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException ex) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            logger.error(ExceptionUtils.getStackTrace(ex));
            producer.close();
        } catch (KafkaException ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            producer.abortTransaction();
        }
        producer.close();
    }

}
