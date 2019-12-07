package blockchain;

import blockchain.vc.VCEntity;

import java.util.Date;

public class Miner implements VCEntity {

    private volatile int vcBalance = 100;

    private final BlockChain blockChain;
    private final Hasher hasher;
    private final String name;

    private Thread miningThread;

    public Miner(int id, BlockChain blockChain, Hasher hasher) {
        this.name = "miner" + id;
        this.blockChain = blockChain;
        this.hasher = hasher;
    }

    public void start(BlockChain.NextBlockInfo nextBlockInfo) {
        miningThread = new Thread(() -> {
            long timestamp = new Date().getTime();
            Block block = BlockGenerator.generate(name, nextBlockInfo, timestamp, hasher);
            boolean added = blockChain.add(block);
            if (added) {
                synchronized (Miner.this) {
                    vcBalance += 100;
                }
                block.minerRewardInfo = name + " gets 100 VC";
                block.nStatus = blockChain.nStatus();
            }
        });
        miningThread.start();
    }

    public void join() throws InterruptedException {
        miningThread.join();
    }


    @Override
    public void updateBalance(int vc) {
        vcBalance = vc;
    }

    @Override
    public int balance() {
        return vcBalance;
    }

    @Override
    public String name() {
        return name;
    }
}