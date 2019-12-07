package blockchain.vc;

import blockchain.message.Message;
import blockchain.message.MessageBroker;
import blockchain.message.MessageEncryptor;
import blockchain.message.SignatureValidator;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TransactionBroker {

    private final Set<VCEntity> entities;
    private final MessageBroker broker;
    private final SignatureValidator messageEncryptor;

    public TransactionBroker(MessageBroker broker, MessageEncryptor messageEncryptor) {
        this.broker = broker;
        this.messageEncryptor = messageEncryptor;
        this.entities = new CopyOnWriteArraySet<>();
    }

    public void registerEntity(VCEntity entity) {
        entities.add(entity);
    }

    public void transaction(Message messageTransaction) throws Exception {
        if (!(messageTransaction.data instanceof Transaction)) {
            throw new IllegalStateException("message data must be a Transaction");
        }
        if (!messageEncryptor.verify(messageTransaction)) {
            return;
        }
        Transaction tx = (Transaction) messageTransaction.data;
        VCEntity from = findEntityByName(tx.fromName);
        if (from == null) {
            return;
        }
        int fromBalance = from.balance();
        for (Transaction.Entry e : tx.toEntries) {
            VCEntity to = findEntityByName(e.toName);
            if (to == null || e.vcAmount <= 0 || fromBalance - e.vcAmount < 0) {
                return; // bail
            }
            fromBalance -= e.vcAmount;
        }

        boolean isQueued = broker.queueMessage(messageTransaction);

        if (isQueued) {
            for (Transaction.Entry e : tx.toEntries) {
                VCEntity to = findEntityByName(e.toName);
                to.updateBalance(to.balance() + e.vcAmount);
                from.updateBalance(from.balance() - e.vcAmount);
            }
        }

    }


    private VCEntity findEntityByName(String name) {
        return entities.stream().filter(e -> e.name().equals(name)).findFirst().orElse(null);
    }
}
