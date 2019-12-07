package blockchain.message;

public interface MessageBroker {

    boolean queueMessage(Message message);

    int requestMessageId();

}
