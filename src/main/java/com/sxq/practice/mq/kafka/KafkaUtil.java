package com.sxq.practice.mq.kafka;

/**
 * Created by s-xq on 2019-12-12.
 */

public class KafkaUtil {

    public static String topicName(String moduleName) {
        return "TopicNameTest-" + moduleName;
    }

    public static String consumerGroupName(String moduleName) {
        return "ConsumerGroupNameTest-" + moduleName;
    }

    public static String stateStore(String moduleName) {
        return "StateStoreNameTest-" + moduleName;
    }

    public static String streamInputTopicName(String moduleName) {
        return topicName(moduleName) + "-Input";
    }

    public static String streamOutputTopicName(String moduleName) {
        return topicName(moduleName) + "-Output";
    }
}
