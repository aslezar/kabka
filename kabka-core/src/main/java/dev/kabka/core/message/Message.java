package dev.kabka.core.message;

public class Message {
    private final String header;
    private final byte[] payload;
    private final String key;

    public Message(String header, byte[] payload, String key) {
        this.header = header;
        this.payload = payload;
        this.key = key;
    }
}
