package dev.kabka.core.storage;

public class Storage {
    private final String header;
    private final byte[] payload;

    public Storage(String header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }
}
