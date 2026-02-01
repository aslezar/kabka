package dev.kabka.core.topic;
import java.util.OptionalInt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kabka.core.message.Message;
import dev.kabka.core.partition.Partition;

public class Topic {
    private final String name;
    private final Partition[] partitions;

    private static final Logger logger = LoggerFactory.getLogger(Topic.class);
    
    public Topic(String name, int noOfPartitions) {
        this.name = name;
        this.partitions = new Partition[noOfPartitions];
        for (int i = 0; i < noOfPartitions; i++) {
            this.partitions[i] = new Partition(name, i);
        }
    }
    
    public String getName() {
        return name;
    }

    public Partition[] getPartitions() {
        return partitions;
    }
 
    public boolean push(Message message, OptionalInt partitionNo) {
        // TODO: Select optimal parition if partition is not provided
        int partNo = partitionNo.orElse(0);
        if (!isValidPartitionNumber(partNo)) {
            logger.error("Invalid partition number "+ partNo + ", topic has "+ this.partitions.length +" partition");
            return false;
        }
        return partitions[partNo].push(message);
    }

    public Message[] pull(int partitionNo, long seqNo, int batchSize) {
        if (!isValidPartitionNumber(partitionNo)) {
            logger.error("Invalid partition number "+ partitionNo + ", topic has "+ this.partitions.length +" partition");
            throw new IllegalArgumentException("Invalid partition number "+ partitionNo + ", topic has "+ this.partitions.length +" partition");
        }
        return partitions[partitionNo].pull(seqNo, batchSize);
    }
    public boolean isValidPartitionNumber(int partitionNumber) {
        return partitionNumber >= 0 && partitionNumber < partitions.length;
    }
}
