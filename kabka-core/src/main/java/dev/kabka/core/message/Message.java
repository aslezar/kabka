package dev.kabka.core.message;

import java.time.Instant;

public class Message {
	private final byte[] payload;
	private final long offset;
	private final Instant timestamp;

	public Message(byte[] payload) {
		this(payload, -1, Instant.now());
	}

	public Message(byte[] payload, long offset, Instant timestamp) {
		this.payload = payload;
		this.offset = offset;
		this.timestamp = timestamp;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String getPayloadString() {
		return new String(payload);
	}

	public long getOffset() {
		return offset;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}
