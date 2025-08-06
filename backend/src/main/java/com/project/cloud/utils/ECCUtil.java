package com.project.cloud.utils;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class ECCUtil {
    /**
     * Generates a new EC key pair using the secp256r1 curve.
     */


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

    /**
     * Rebuilds a KeyPair from encoded public and private key bytes.
     */
    public KeyPair rebuildKeyPair(byte[] publicKeyBytes, byte[] privateKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        return new KeyPair(publicKey, privateKey);
    }
}
