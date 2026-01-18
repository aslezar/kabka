package dev.kabka.core.partition;

public class Partition {
    private final int partitionNo;
    private final java.util.Queue<byte[]> messages = new java.util.LinkedList<>();

    public Partition(int partitionNo) {
        this.partitionNo = partitionNo;
    }
}
