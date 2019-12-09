package com.sxq.practice.mq.rocketmq;

/**
 * Created by s-xq on 2019-12-09.
 */

public class MqUtil {


    public static String producerGroupName(String exampleModuleName) {
        return "GroupNameTest-" + exampleModuleName;
    }

    public static String topicName(String exampleModuleName) {
        return "TopicNameTest-" + exampleModuleName;
    }

    public static String tagName(String exampleModuleName) {
        return "TagNameTest-" + exampleModuleName;
    }

    public static String consumerGroupName(String exampleModuleName) {
        return "ConsumerGroupNameTest-" + exampleModuleName;
    }


    public static String keysName(String exampleModuleName) {
        return "KeysNameTest-" + exampleModuleName;
    }

}
