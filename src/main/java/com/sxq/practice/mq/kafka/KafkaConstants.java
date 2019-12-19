package com.sxq.practice.mq.kafka;

import com.sxq.practice.mq.kafka.rebalance.HandleRebalanceConsumer;
import com.sxq.practice.mq.kafka.rebalance.HandleRebalanceUsingDbTransaction;

/**
 * Created by s-xq on 2019-12-12.
 */

public class KafkaConstants {

    public static final String BOOTSTRAP_SERVERS = "localhost:9093,localhost:9094";

    public static class ExampleModule {
        public static final String MODULE_SIMPLE = "Simple";
        public static final String MODULE_IDEMPOTENCE = "Idempotence";
        public static final String MODULE_TRANSACTIONAL = "Transactional";
        public static final String MODULE_AUTO_OFFSET_COMMIT = "AutoOffsetCommitConsumer";
        public static final String MODULE_MANUAL_OFFSET_CONTROL = "ManualOffsetControlCommitConsumer";
        public static final String MODULE_HANDLE_REBALANCE = HandleRebalanceConsumer.class.getSimpleName();
        public static final String MODULE_HANDLE_REBALANCE_UNSING_DB_TRANSACTION
                = HandleRebalanceUsingDbTransaction.class.getSimpleName();
        public static final String MODULE_STREAM_WORD_COUNT = "StreamWordCount";

    }

}
