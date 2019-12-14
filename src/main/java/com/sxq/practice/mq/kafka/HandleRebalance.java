package com.sxq.practice.mq.kafka;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;

/**
 * Created by s-xq on 2019-12-14.
 */

public class HandleRebalance implements ConsumerRebalanceListener {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private Consumer consumer;

    public HandleRebalance(
            Map<TopicPartition, OffsetAndMetadata> currentOffsets, Consumer consumer) {
        this.currentOffsets = currentOffsets;
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        /**
         * commit offset before partition reassigned
         */
        logger.info("partitions will be rebalanced. Current committing offsets of assigned partition is:[{}], "
                        + "current assign partition:[{}]",
                currentOffsets.toString(), consumer.assignment());
        consumer.commitSync(currentOffsets);
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.info("[{}], partition assigned, begin to consume message, currentOffsets:[{}], "
                        + "current assign partition:[{}]",
                consumer.toString(), currentOffsets.toString(), consumer.assignment());
    }
}
