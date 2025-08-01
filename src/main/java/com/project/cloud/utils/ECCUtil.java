package com.project.cloud.utils;

import org.springframework.stereotype.Component;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class ECCUtil {

    private final KeyPair staticKeyPair;

    public ECCUtil() throws Exception {
        this.staticKeyPair = generateKeyPair(); // static server keypair
    }

    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(new ECGenParameterSpec("secp256r1"));
        return keyPairGen.generateKeyPair();
    }

    public SecretKey deriveSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);

        byte[] sharedSecret = keyAgreement.generateSecret();
        // use first 16 bytes (128-bit key) for AES
        return new SecretKeySpec(sharedSecret, 0, 16, "AES");
    }

    public PublicKey rebuildPublicKey(byte[] encodedPublicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
    }

    public KeyPair getStaticKeyPair() {
        return staticKeyPair;
    }
}
