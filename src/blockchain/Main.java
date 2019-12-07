package blockchain;

import blockchain.message.Message;
import blockchain.message.MessageEncryptor;
import blockchain.vc.Transaction;
import blockchain.vc.TransactionBroker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        Hasher hasher = new Hasher.SHA256Hasher();
        //File file = new File("block-chain.bch");
        StartingZeroesRegulator nRegulator = new StartingZeroesRegulator.DefaultStartingZeroesRegulator(0);
        BlockChain blockChain = new BlockChain(nRegulator);

        MessageEncryptor messageEncryptor = new MessageEncryptor();
        TransactionBroker txBroker = new TransactionBroker(blockChain, messageEncryptor);
        List<Miner> miners = miners(10, blockChain, hasher);
        miners.forEach(txBroker::registerEntity);

        Transaction tx = new Transaction(
                miners.get(2).name(),
                new Transaction.Entry(miners.get(3).name(), 50)
        );
        Message txMessage = messageEncryptor.sign(
                Message.createUnsigned(blockChain.requestMessageId(), tx));
        txBroker.transaction(txMessage);

        for (long i = 0; i < 15; i++) {
            for (Miner miner : miners) {
                miner.start(blockChain.requestNextInfo());
            }
            for (Miner miner : miners) {
                miner.join();
            }
            // blockChain.serialize(file);
            System.out.println(blockChain.latest());
            System.out.println();
        }

    }

    private static List<Miner> miners(int number,
                                      BlockChain blockChain,
                                      Hasher hasher) {
        ArrayList<Miner> miners = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            miners.add(new Miner(i + 1, blockChain, hasher));
        }
        return miners;
    }
}