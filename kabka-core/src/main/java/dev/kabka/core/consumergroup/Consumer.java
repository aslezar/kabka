// package dev.kabka.core.consumergroup;
// import dev.kabka.core.message.Message;
// import dev.kabka.core.partition.Partition;

// public class ConsumerGroup {
//     private final String name;
//     private final Partition[] partitions;
    
//     public ConsumerGroup(String name, int noOfPartitions) {
//         this.name = name;
//         this.partitions = new Partition[noOfPartitions];
//     }
    
//     public String getName() {
//         return name;
//     }

//     public boolean push(Message message, int partitionNo) {
//         // no-op for now
//         return false;
//     }

//     public Message[] pull(int partitionNo, int seqNo, int batchSize) {
//         return new Message[batchSize];
//     }
// }
