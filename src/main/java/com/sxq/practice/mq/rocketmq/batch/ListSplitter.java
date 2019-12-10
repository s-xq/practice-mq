package com.sxq.practice.mq.rocketmq.batch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.common.message.Message;

/**
 * Created by s-xq on 2019-12-10.
 */

public class ListSplitter implements Iterator<List<Message>> {

    private final int SIZE_LIMIT = 1000 * 1000;
    private final List<Message> messages;
    private int curIndex;

    public ListSplitter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean hasNext() {
        return curIndex <= messages.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = curIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            int tempSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tempSize += entry.getKey().length() + entry.getValue().length();
            }
            tempSize += 20;
            if (tempSize > SIZE_LIMIT) {
                if (nextIndex - curIndex == 0) {
                    nextIndex++;
                }
                break;
            }
            if (tempSize + totalSize > SIZE_LIMIT) {
                break;
            } else {
                totalSize += tempSize;
            }
        }
        List<Message> subList = messages.subList(curIndex, nextIndex);
        curIndex = nextIndex;
        return subList;
    }
}
