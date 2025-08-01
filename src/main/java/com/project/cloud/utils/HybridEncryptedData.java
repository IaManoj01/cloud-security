package com.project.cloud.utils;

public class HybridEncryptedData {
    public byte[] ephemeralPublicKey;
    public byte[] iv;
    public byte[] cipherText;

    public HybridEncryptedData() {
    }

    public HybridEncryptedData(byte[] ephemeralPublicKey, byte[] iv, byte[] cipherText) {
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.iv = iv;
        this.cipherText = cipherText;
    }
}
