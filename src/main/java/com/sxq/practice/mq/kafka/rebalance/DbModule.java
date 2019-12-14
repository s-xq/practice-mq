package com.sxq.practice.mq.kafka.rebalance;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

/**
 * Created by s-xq on 2019-12-14.
 */

public class DbModule {

    public static void commitDbTransaction() {
        /**
         * TODO implementation
         */
    }

    public static long getOffsetFromDb(TopicPartition topicPartition) {
        /**
         * TODO implementation
         */
        return 0;
    }

    public static void storeRecordInDb(ConsumerRecord consumerRecord) {
        /**
         * TODO implementation
         */
    }

    public static void storeOffsetInDb(String topic, int partition, long offset) {

    }
}
