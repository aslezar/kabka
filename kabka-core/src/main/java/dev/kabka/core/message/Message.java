package dev.kabka.core.message;

public class Message {
	// private final String header;
	private final byte[] payload;
	// private final String key;

	public Message(byte[] payload) {
		// this.header = header;
		this.payload = payload;
		// this.key = key;
	}

	public String getPayloadString() {
		return new String(payload);
	}
}
