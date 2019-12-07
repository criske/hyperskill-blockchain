package blockchain;

public class BlockGenerator {

    private BlockGenerator() {
        throw new IllegalStateException("BlockGenerator is uninstantiable");
    }

    public static Block generate(
            String minerName,
            BlockChain.NextBlockInfo info,
            long timestamp,
            Hasher hasher) {
        Hasher.Result result = hasher.hash(
                info.startingZeroes,
                minerName,
                Integer.toString(info.id),
                info.previousHash,
                Long.toString(timestamp),
                (info.messageData == null) ? "" : info.messageData.toString());
        return new Block(info.id,
                minerName,
                timestamp,
                result.magicNumber,
                result.hash,
                info.previousHash,
                result.timeSpentSeconds,
                info.messageData);
    }

}
