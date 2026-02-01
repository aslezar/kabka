package dev.kabka.core.partition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kabka.core.message.Message;

public class Partition {
    private final String topicName;
    private final int partitionNo;
    private final java.util.ArrayList<Message> messages = new java.util.ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(Partition.class);

    public Partition(String topicName, int partitionNo) {
        this.topicName = topicName;
        this.partitionNo = partitionNo;
    }

    public String getTopicName() {
        return topicName;
    }

    public boolean push(Message message) {
        try {
            messages.add(message);
            return true;
        } catch (IllegalStateException e) {
            logger.error("Failed to push message to partition " + partitionNo, e);
            return false;
        }
    }

    public Message[] pull(long seqNo, int batchSize) {
        if (seqNo < 0 || seqNo >= messages.size()) {
            logger.error("Invalid sequence number " + seqNo + " for partition " + partitionNo);
            throw new IllegalArgumentException("Invalid sequence number " + seqNo + " for partition " + partitionNo);
        }

        int endIndex = Math.min((int)seqNo + batchSize, messages.size());
        return messages.subList((int)seqNo, endIndex).toArray(Message[]::new);
    }
}
