package blockchain;

import java.io.Serializable;

public class Block implements Serializable, Comparable<Block> {

    public static final String NO_HASH = "0";

    public final int id;
    public final String minerName;
    public final long timestamp;
    public final int magicNumber;
    public final String hash;
    public final String previousHash;
    public final int timeSpentSeconds;
    public final Object data;

    public String nStatus = "";
    public String minerRewardInfo = "";

    public Block(int id,
                 String minerName,
                 long timestamp,
                 int magicNumber,
                 String hash,
                 String previousHash,
                 int timeSpentSeconds,
                 Object data) {
        this.id = id;
        this.minerName = minerName;
        this.timestamp = timestamp;
        this.magicNumber = magicNumber;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timeSpentSeconds = timeSpentSeconds;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Block:\n" +
                "Created by miner " + minerName + "\n" +
                minerRewardInfo + "\n" +
                "Id: " + id + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "Magic number: " + magicNumber + "\n" +
                "Hash of the previous block:\n" + previousHash + "\n" +
                "Hash of the block:\n" + hash + "\n" +
                "Block data: \n" + ((data == null) ? "No transactions" : data.toString()) + "\n" +
                "Block was generating for " + timeSpentSeconds + " seconds\n" +
                nStatus;
    }

    @Override
    public int compareTo(Block o) {
        return Integer.compare(id, o.id);
    }
}
