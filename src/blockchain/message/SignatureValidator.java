package blockchain.message;

public interface SignatureValidator {

    boolean verify(Message message)  throws Exception;
}
