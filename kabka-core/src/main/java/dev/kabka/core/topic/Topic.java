package dev.kabka.core.topic;
import dev.kabka.core.partition.Partition;
import dev.kabka.core.message.Message;

public class Topic {
    private final String name;
    private final Partition[] partitions;
    
    public Topic(String name, int noOfPartitions) {
        this.name = name;
        this.partitions = new Partition[noOfPartitions];
        for (int i = 0; i < noOfPartitions; i++) {
            this.partitions[i] = new Partition(i);
        }
    }
    
    public String getName() {
        return name;
    }

    public boolean push(Message message, int partitionNo) {
        // no-op for now
        return false;
    }

    public Message[] pull(int partitionNo, int seqNo, int batchSize) {
        return new Message[batchSize];
    }
}
