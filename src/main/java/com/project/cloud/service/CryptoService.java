package com.project.cloud.service;

import com.project.cloud.utils.AESUtil;
import com.project.cloud.utils.ECCUtil;
import com.project.cloud.utils.HybridEncryptedData;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PublicKey;

@Service
public class CryptoService {

    private final ECCUtil eccUtil;
    private final AESUtil aesUtil;
    private KeyPair serverKeyPair;  // ECC key pair for this service

    public CryptoService(ECCUtil eccUtil, AESUtil aesUtil) {
        this.eccUtil = eccUtil;
        this.aesUtil = aesUtil;
    }

    @PostConstruct
    public void init() throws Exception {
        this.serverKeyPair = eccUtil.generateKeyPair();  // create ECC key pair on startup
    }

    // Encrypt data with a new ephemeral key (like the client would do)
    public HybridEncryptedData hybridEncrypt(byte[] plainText) throws Exception {
        KeyPair ephemeralKeyPair = eccUtil.generateKeyPair(); // temporary key for encryption

        // Derive shared secret using receiver's (server's) public key
        SecretKey sharedSecret = eccUtil.deriveSharedSecret(ephemeralKeyPair.getPrivate(), serverKeyPair.getPublic());

        // Encrypt data with AES using shared secret
        AESUtil.EncryptionResult result = aesUtil.encrypt(plainText, sharedSecret);

        // Package the ephemeral public key and AES result
        return new HybridEncryptedData(
                ephemeralKeyPair.getPublic().getEncoded(),  // send this to receiver
                result.getIv(),
                result.getCipherText()
        );
    }

    // Decrypt using own private key and received ephemeral public key
    public byte[] hybridDecrypt(HybridEncryptedData data) throws Exception {
        PublicKey ephemeralKey = eccUtil.rebuildPublicKey(data.ephemeralPublicKey);

        // Derive the shared secret using own private key and received ephemeral public key
        SecretKey sharedSecret = eccUtil.deriveSharedSecret(serverKeyPair.getPrivate(), ephemeralKey);

        return aesUtil.decrypt(data.cipherText, sharedSecret, data.iv);
    }

    // Optional: expose server public key if needed by clients
    public byte[] getServerPublicKeyEncoded() {
        return serverKeyPair.getPublic().getEncoded();
    }
}
