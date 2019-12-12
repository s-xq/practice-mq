package com.sxq.practice.mq.kafka;

/**
 * Created by s-xq on 2019-12-12.
 */

public class KafkaUtil {

    public static String topicName(String moduleName) {
        return "TopicNameTest-" + moduleName;
    }
}
