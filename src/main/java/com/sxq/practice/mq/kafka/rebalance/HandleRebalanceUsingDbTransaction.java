package com.sxq.practice.mq.kafka.rebalance;

import java.util.Collection;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.kafka.rebalance.DbModule;

/**
 * Created by s-xq on 2019-12-14.
 */

public class HandleRebalanceUsingDbTransaction implements ConsumerRebalanceListener {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    private Consumer consumer;

    public HandleRebalanceUsingDbTransaction(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        /**
         * commit offset to db before partition reassigned
         */
        logger.info("partitions will be rebalanced. Committing offsets into db, current assign partition:[{}]",
                consumer.assignment());
        DbModule.commitDbTransaction();
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.info("partition assigned, begin to consume message, current assign partition:[{}]",
                consumer.assignment());
        for (TopicPartition topicPartition : partitions) {
            consumer.seek(topicPartition, DbModule.getOffsetFromDb(topicPartition));
        }
    }

}
