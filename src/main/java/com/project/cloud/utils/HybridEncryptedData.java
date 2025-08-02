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

    public byte[] getEphemeralPublicKey() {
        return ephemeralPublicKey;
    }

    public void setEphemeralPublicKey(byte[] ephemeralPublicKey) {
        this.ephemeralPublicKey = ephemeralPublicKey;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getCipherText() {
        return cipherText;
    }

    public void setCipherText(byte[] cipherText) {
        this.cipherText = cipherText;
    }
}
