package blockchain.message;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class MessageEncryptor implements SignatureValidator {

    private final File keystoreDir = new File("keystore");
    private final File publicKeyFile = new File(keystoreDir, "publicKey");
    private final File privateKeyFile = new File(keystoreDir, "privateKey");

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public MessageEncryptor() throws Exception {
        initKeys();
    }

    private void initKeys() throws Exception {
        if (!keystoreDir.exists()) {
            if (!keystoreDir.mkdir()) {
                throw new IllegalStateException("Could not create keystore dir");
            }
        }
        if (!publicKeyFile.exists() || privateKeyFile.exists()) {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            FileOutputStream fos = new FileOutputStream(publicKeyFile);
            fos.write(publicKey.getEncoded());
            fos.flush();
            fos.close();

            fos = new FileOutputStream(privateKeyFile);
            fos.write(privateKey.getEncoded());
            fos.flush();
            fos.close();

        } else {

            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        }
    }


    public Message sign(Message message) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(privateKey);
        rsa.update(message.data.toString().getBytes());
        return new Message(message.id, message.data, rsa.sign());
    }

    @Override
    public boolean verify(Message message) throws Exception {
        if (message.signature.length != 128) {
            return false;
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(publicKey);
        sig.update(message.data.toString().getBytes());
        return sig.verify(message.signature);
    }
}
