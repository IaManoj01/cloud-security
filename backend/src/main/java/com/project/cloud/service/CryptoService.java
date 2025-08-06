package com.project.cloud.service;

import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.project.cloud.utils.AESUtil;
import com.project.cloud.utils.ECCUtil;
import com.project.cloud.utils.HybridEncryptedData;

import jakarta.annotation.PostConstruct;

@Service
public class CryptoService {

    private final ECCUtil eccUtil;
    private final AESUtil aesUtil;
    private KeyPair serverKeyPair;  // ECC key pair for this service
    private static final String PRIVATE_KEY_FILE = "server_ecc_private.key";
    private static final String PUBLIC_KEY_FILE = "server_ecc_public.key";

    public CryptoService(ECCUtil eccUtil, AESUtil aesUtil) {
        this.eccUtil = eccUtil;
        this.aesUtil = aesUtil;
    }

    @PostConstruct
    public void init() throws Exception {
        java.io.File privFile = new java.io.File(PRIVATE_KEY_FILE);
        java.io.File pubFile = new java.io.File(PUBLIC_KEY_FILE);
        if (privFile.exists() && pubFile.exists()) {
            // Load keys from disk
            byte[] privBytes = java.nio.file.Files.readAllBytes(privFile.toPath());
            byte[] pubBytes = java.nio.file.Files.readAllBytes(pubFile.toPath());
            this.serverKeyPair = eccUtil.rebuildKeyPair(pubBytes, privBytes);
        } else {
            // Generate and save new key pair
            this.serverKeyPair = eccUtil.generateKeyPair();
            java.nio.file.Files.write(privFile.toPath(), serverKeyPair.getPrivate().getEncoded());
            java.nio.file.Files.write(pubFile.toPath(), serverKeyPair.getPublic().getEncoded());
        }
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
