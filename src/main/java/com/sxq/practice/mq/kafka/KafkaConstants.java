package com.sxq.practice.mq.kafka;

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
        public static final String MODULE_HANDLE_REBALANCE = "HandleRebalanceConsumer";

    }

}
