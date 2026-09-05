package dev.kabka.core.partition;

import dev.kabka.core.exception.InvalidOffsetException;
import dev.kabka.core.message.Message;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partition {
	private final String topicName;
	private final int partitionNo;
	private final List<Message> messages = new ArrayList<>();
	private final Map<String, Long> committedOffsets = new HashMap<>();

	public Partition(String topicName, int partitionNo) {
		this.topicName = topicName;
		this.partitionNo = partitionNo;
	}

	public String getTopicName() {
		return topicName;
	}

	public int getPartitionNo() {
		return partitionNo;
	}

	public synchronized long push(Message message) {
		long offset = messages.size();
		messages.add(new Message(message.getPayload(), offset, Instant.now()));
		return offset;
	}

	public synchronized Message[] pull(long seqNo, int batchSize) {
		if (seqNo < 0 || seqNo >= messages.size()) {
			throw new InvalidOffsetException("Invalid sequence number " + seqNo + " for partition " + partitionNo);
		}
		return slice(seqNo, batchSize);
	}

	public synchronized Message[] poll(String groupName, int batchSize) {
		long offset = committedOffsets.getOrDefault(groupName, 0L);
		if (offset >= messages.size()) {
			return new Message[0];
		}
		return slice(offset, batchSize);
	}

	public synchronized void commitOffset(String groupName, long offset) {
		if (offset < 0 || offset > messages.size()) {
			throw new InvalidOffsetException("Invalid commit offset " + offset + " for partition " + partitionNo);
		}
		committedOffsets.put(groupName, offset);
	}

	public synchronized long getCommittedOffset(String groupName) {
		return committedOffsets.getOrDefault(groupName, 0L);
	}

	public synchronized int size() {
		return messages.size();
	}

	private Message[] slice(long seqNo, int batchSize) {
		int endIndex = Math.min((int) seqNo + batchSize, messages.size());
		return messages.subList((int) seqNo, endIndex).toArray(Message[]::new);
	}
}
