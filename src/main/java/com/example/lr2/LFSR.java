package com.example.lr2;

import java.io.*;

public class LFSR {
    private final int[] polinom =  {38, 6, 5, 1} ;
    protected long register;
    private long currRegister;
    private long mask;

    private String getOutEncodePath(String filePath) {
        return filePath + ".encoded";
    }

    private String getOutDecodePath(String filePath) {
        return filePath + ".decoded";
    }

    private String getOutKeyPath(String filePath) {
        return filePath + "-bites.txt";
    }


    public LFSR(String initRegister) {
        register = Long.parseLong(initRegister, 2);
        generateMask();
    }

    public static String keyToStr(byte[] key, int bytesCount) {
        StringBuilder strKey = new StringBuilder();
        if (bytesCount > key.length) {
            bytesCount = key.length;
        }
        for (int i = 0; i < bytesCount; i++) {
            StringBuilder binaryByte = new StringBuilder(Integer.toBinaryString(key[i] & 255));
            for (int j = binaryByte.length(); j < 8; j++) {
                binaryByte.insert(0, "0");
            }
            strKey.append(binaryByte);
        }
        return strKey.toString();
    }


    private byte getBitAtPos(int pos) {
        return (byte) ((byte) (currRegister >> pos - 1) & 1);
    }


    private void generateMask() {
        mask = Long.parseLong("1".repeat(Math.max(0, polinom[0])), 2);
    }


    public byte[] generateKey(int len) {
        currRegister = register;
        byte[] key = new byte[len];
        for (int i = 0; i < key.length; i++) { // получаем полный ключ
            for (int j = 0; j < 8; j++) {

                byte abortedBit = getBitAtPos(polinom[0]); // Бит, который мы выкидываем
                key[i] = (byte) (key[i] | (abortedBit << (8 - j - 1))); // Записываем выкинутый бит в ключ

                byte newFirstBit = abortedBit; // Рассчитываем новый первый бит как xor битов, заданных полиноном
                for (int k = 1; k < polinom.length; k++) {
                    newFirstBit ^= getBitAtPos(polinom[k]);
                }
                currRegister = (currRegister << 1) & mask; // Сдвигаем регистр
                currRegister = currRegister | newFirstBit; // Устанавливаем первый бит согласно вычислениям
            }
        }
        return key;
    }

    public byte[] encrypt(byte[] plainBytes) {
        byte[] key = generateKey(plainBytes.length);
        byte[] cipherBytes = new byte[plainBytes.length];
        for (int i = 0; i < plainBytes.length; i++) {
            cipherBytes[i] = (byte) (plainBytes[i] ^ key[i]);
        }
        return cipherBytes;
    }

    public byte[] decrypt(byte[] cipherBytes) {
        return encrypt(cipherBytes);
    }

    private byte[] readFile(String filePath) throws IOException {
        byte[] content;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            content = fis.readAllBytes();
            fis.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
        return content;

    }

    private void writeFile(String filePath, byte[] content) throws IOException {

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String[] encode(String filePath) throws IOException {
        byte[] plainText = readFile(filePath);
        byte[] cipherText = this.encrypt(plainText);
        writeFile(getOutEncodePath(filePath), cipherText);

        byte[] key = this.generateKey(plainText.length);
        String strKey = LFSR.keyToStr(key, key.length);
        String strCipher = LFSR.keyToStr(cipherText, plainText.length);
        String strPlain = LFSR.keyToStr(plainText, cipherText.length);
        writeFile(getOutKeyPath(filePath), strKey.getBytes());

        return new String[]{strKey, strCipher, strPlain};
    }

    public String[] decode(String filePath) throws IOException {
        byte[] cipherText = readFile(filePath);
        byte[] plainText = this.encrypt(cipherText);
        writeFile(getOutDecodePath(filePath), plainText);

        byte[] key = this.generateKey(plainText.length);
        String strKey = LFSR.keyToStr(key, key.length);
        String plainStr = LFSR.keyToStr(plainText, plainText.length);
        String strCipher = LFSR.keyToStr(cipherText, cipherText.length);
        writeFile(getOutKeyPath(filePath), strKey.getBytes());

        return new String[]{strKey, plainStr, strCipher};
    }



}
