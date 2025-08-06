package com.project.cloud.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class HybridCryptoUtil {

    public static byte[] packHybridData(HybridEncryptedData data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(data.getEphemeralPublicKey().length);
        dos.write(data.getEphemeralPublicKey());

        dos.writeInt(data.getIv().length);
        dos.write(data.getIv());

        dos.writeInt(data.getCipherText().length);
        dos.write(data.getCipherText());

        return out.toByteArray();
    }


    public static HybridEncryptedData unpackHybridData(byte[] packed) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(packed);
        DataInputStream dis = new DataInputStream(in);

        try {
            int ephLen = dis.readInt();
            // Increase the maximum allowed length to accommodate larger keys
            // ECC keys can be larger depending on the curve and encoding
            if (ephLen < 0 || ephLen > 2048) {
                throw new IOException("Invalid ephemeral key length: " + ephLen);
            }

            byte[] ephemeralPublicKey = new byte[ephLen];
            dis.readFully(ephemeralPublicKey);

            int ivLen = dis.readInt();
            if (ivLen < 0 || ivLen > 64) throw new IOException("Invalid IV length: " + ivLen);

            byte[] iv = new byte[ivLen];
            dis.readFully(iv);

            int cipherLen = dis.readInt();
            if (cipherLen < 0 || cipherLen > (10 * 1024 * 1024)) throw new IOException("Invalid cipher length: " + cipherLen);

            byte[] cipherText = new byte[cipherLen];
            dis.readFully(cipherText);

            return new HybridEncryptedData(ephemeralPublicKey, iv, cipherText);
        } catch (EOFException e) {
            throw new IOException("Corrupted or invalid encrypted data format", e);
        }
    }
}
