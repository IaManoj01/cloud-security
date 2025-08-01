package com.project.cloud.utils;

import java.io.*;

public class HybridCryptoUtil {

    public static byte[] packHybridData(HybridEncryptedData data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeInt(data.ephemeralPublicKey.length);
        dos.write(data.ephemeralPublicKey);

        dos.writeInt(data.iv.length);
        dos.write(data.iv);

        dos.writeInt(data.cipherText.length);
        dos.write(data.cipherText);

        dos.close();
        return out.toByteArray();
    }

    public static HybridEncryptedData unpackHybridData(byte[] packed) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(packed);
        DataInputStream dis = new DataInputStream(in);

        byte[] epk = new byte[dis.readInt()];
        dis.readFully(epk);

        byte[] iv = new byte[dis.readInt()];
        dis.readFully(iv);

        byte[] cipher = new byte[dis.readInt()];
        dis.readFully(cipher);

        dis.close();
        return new HybridEncryptedData(epk, iv, cipher);
    }
}
