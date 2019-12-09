package com.sxq.practice.mq.rocketmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by s-xq on 2019-12-09.
 */

@Configuration
@Getter
@Setter
@ToString
public class RocketMqConfig {

    @Value("${app.rocketmq.name-srv-addr}")
    private String nameSrvAddress;

}
