package dev.kabka.core.topic;

import dev.kabka.core.exception.InvalidPartitionException;
import dev.kabka.core.message.Message;
import dev.kabka.core.partition.Partition;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic {
	private final String name;
	private final Partition[] partitions;
	private final AtomicInteger roundRobin = new AtomicInteger(0);

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

	public Partition getPartition(int partitionNo) {
		if (!isValidPartitionNumber(partitionNo)) {
			throw new InvalidPartitionException("Invalid partition number " + partitionNo + ", topic has "
					+ this.partitions.length + " partitions");
		}
		return partitions[partitionNo];
	}

	public PushResult push(Message message, OptionalInt partitionNo) {
		int partNo = partitionNo.orElseGet(() -> Math.floorMod(roundRobin.getAndIncrement(), partitions.length));
		long offset = getPartition(partNo).push(message);
		return new PushResult(partNo, offset);
	}

	public Message[] pull(int partitionNo, long seqNo, int batchSize) {
		return getPartition(partitionNo).pull(seqNo, batchSize);
	}

	public boolean isValidPartitionNumber(int partitionNumber) {
		return partitionNumber >= 0 && partitionNumber < partitions.length;
	}
}
