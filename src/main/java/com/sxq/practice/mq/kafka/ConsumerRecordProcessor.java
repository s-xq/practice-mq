package com.sxq.practice.mq.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-14.
 */

public class ConsumerRecordProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    private ConsumerRecord consumerRecord;

    public ConsumerRecordProcessor(ConsumerRecord consumerRecord) {
        this.consumerRecord = consumerRecord;
    }

    public void process() {
        logger.info("topic:[{}], \tpartition:[{}], \toffset:[{}], \tkey:[{}], \tvalue:[{}]",
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key(),
                consumerRecord.value());
    }
}
