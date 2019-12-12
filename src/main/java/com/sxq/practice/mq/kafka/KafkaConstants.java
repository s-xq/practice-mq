package com.sxq.practice.mq.kafka;

/**
 * Created by s-xq on 2019-12-12.
 */

public class KafkaConstants {

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static class ExampleModule {
        public static final String MODULE_SIMPLE = "Simple";
        public static final String MODULE_IDEMPOTENCE = "Idempotence";
        public static final String MODULE_TRANSACTIONAL = "Transactional";

    }

}
