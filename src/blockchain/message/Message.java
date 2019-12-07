package blockchain.message;

public class Message {

    public final int id;
    public final Object data;
    public final byte[] signature;

    public Message(int id, Object data, byte[] signature) {
        this.id = id;
        this.data = data;
        this.signature = signature;
    }

    public static Message createUnsigned(int id, Object data) {
        return new Message(id, data, new byte[0]);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
