package com.sxq.practice.mq.kafka.stream;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxq.practice.mq.Constants;
import com.sxq.practice.mq.kafka.KafkaConstants;
import com.sxq.practice.mq.kafka.KafkaUtil;

/**
 * Created by s-xq on 2019-12-18.
 * {@link https://kafka.apache.org/24/documentation/streams/tutorial}
 */

public class WordCountTaskProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Constants.LogName.KAFKA);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, WordCountTaskProcessor.class.getSimpleName());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(KafkaUtil.streamInputTopicName(
                KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT));
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(KafkaUtil.stateStore(
                        KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT)));
        wordCounts.toStream().to(KafkaUtil.streamInputTopicName(KafkaConstants.ExampleModule.MODULE_STREAM_WORD_COUNT),
                Produced.with(Serdes.String(), Serdes.Long()));
        Topology topology = builder.build();
        logger.info("topology:[{}]", topology.describe());
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
}
