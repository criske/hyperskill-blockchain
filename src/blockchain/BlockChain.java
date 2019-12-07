package blockchain;

import blockchain.message.Message;
import blockchain.message.MessageBroker;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockChain implements Iterable<Block>, MessageBroker {

    private final Map<String, Block> chain;
    private final StartingZeroesRegulator nRegulator;
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private volatile String previousHash = Block.NO_HASH;
    private final Random random = new Random();
    //TODO: make thread safe when needed
    private int currentMessageId = 1;

    @SuppressWarnings("unchecked")
    public static BlockChain createFromFile(StartingZeroesRegulator nRegulator, File file) {
        HashMap<String, Block> chain;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            chain = (HashMap<String, Block>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
            //fallback
            chain = new HashMap<>();
        }
        return new BlockChain(nRegulator, chain);
    }

    private BlockChain(StartingZeroesRegulator nRegulator, Map<String, Block> chain) {
        this.chain = new ConcurrentHashMap<>(chain);
        this.nRegulator = nRegulator;
        Block lastBlock = chain.values().stream().max(Block::compareTo).orElse(null);
        if (lastBlock != null) {
            idGenerator.set(lastBlock.id);
            previousHash = lastBlock.hash;
        }
    }

    public BlockChain(StartingZeroesRegulator nRegulator) {
        this(nRegulator, new HashMap<>());
    }

    public void serialize(File destination) {
        try {
            FileOutputStream fos =
                    new FileOutputStream(destination);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new HashMap<>(chain));
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean isValid() {
        boolean valid = true;
        Block current = chain.get(previousHash);
        String prevHash = current.previousHash;
        while (!prevHash.equals(Block.NO_HASH)) {
            current = chain.get(prevHash);
            if (current == null) {
                valid = false;
                break;
            }
            prevHash = current.previousHash;
        }
        return valid;
    }

    public Block latest() {
        return chain.get(previousHash);
    }

    public String nStatus() {
        return nRegulator.status();
    }

    @Override
    public boolean queueMessage(Message message) {
        if (message.id < currentMessageId) {
            return false;
        }
        return messageQueue.add(message);
    }

    @Override
    public int requestMessageId() {
        int generated;
        do {
            generated = random.nextInt();
        } while (generated < currentMessageId);
        currentMessageId = generated;
        return currentMessageId;
    }


    public NextBlockInfo requestNextInfo() {
        int id = idGenerator.get();
        int startingZeroes = nRegulator.getStartingZeroes();
        Message message = messageQueue.peek();
        Object messageData = (message == null) ? null : message.data;
        return new NextBlockInfo(id + 1, previousHash, startingZeroes, messageData);
    }


    public synchronized boolean add(Block block) {
        Block lastBlock = chain.get(previousHash);
        if (lastBlock == null || block.previousHash.equals(lastBlock.hash)) {
            nRegulator.regulate(block.timeSpentSeconds);
            chain.put(block.hash, block);
            previousHash = block.hash;
            idGenerator.incrementAndGet();
            messageQueue.poll();
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<Block> iterator() {
        return chain.values().stream().sorted().iterator();
    }

    public static class NextBlockInfo {
        public final int id;
        public final String previousHash;
        public final int startingZeroes;
        public final Object messageData;

        public NextBlockInfo(int id, String previousHash, int startingZeroes, Object messageData) {
            this.id = id;
            this.previousHash = previousHash;
            this.startingZeroes = startingZeroes;
            this.messageData = messageData;
        }
    }

}
