package com.example.storage;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//я у мамы криптограф^_^
class MYCryptography {

    public static final boolean MODE_ENCRYPT = true;
    public static final boolean MODE_DECRYPT = false;
    private static volatile MYCryptography instance;
    private final SecretKey key;
    private volatile Cipher cipher;

    private volatile boolean is_encryption_mode;

    //the constructor creates a key object and initializes the cipher object
    private MYCryptography(byte[] _key) {
        key = new SecretKeySpec(_key,"AES");
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) { e.printStackTrace();
        } catch (NoSuchPaddingException e) { e.printStackTrace(); }
    }

    //our program have only one object responsible for cryptography
    //this is a singleton
    public static MYCryptography getInstance(byte[] _key) {
        if(instance == null) synchronized (MYCryptography.class){
            if(instance == null) instance = new MYCryptography(_key);
        }
        return instance;
    }

    //changing the mode encryption/decryption
    public void init(boolean _mode) {
        try {
            if (_mode) { cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(key.getEncoded(), 1, 17))); is_encryption_mode = true; }
            else { cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(key.getEncoded(), 1, 17))); is_encryption_mode = false; }
        } catch (InvalidAlgorithmParameterException e) { e.printStackTrace();
        } catch (InvalidKeyException e) { e.printStackTrace(); }
    }

    public boolean get_mode() { return is_encryption_mode; }


    //generating a key according to the AES standard
    public static byte[] generateKey() {

        SecretKey key;

        byte[] _key = {};

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance("AES", new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        SecureRandom secureRandom = new SecureRandom();
            int keyBitSize = 256;
            keyGenerator.init(keyBitSize, secureRandom);
            key = keyGenerator.generateKey();

            _key = key.getEncoded();
        return _key;
    }

    //passing the key and password as a string
    //getting an encrypted key
    public static byte[] encryptKey(byte[] _key, String _pass){

        //user pass to byte
        byte[] pass = _pass.getBytes(StandardCharsets.UTF_8);

        int[] int_pass = new int[32];
        int j = 0;
        for (int i = 0; i < int_pass.length; i++) {
            int_pass[i] = _key[i] * pass[j];
            int_pass[i] += pass[j];
            int_pass[i] *= pass[j];
            int_pass[i] += pass[j];
            int_pass[i] *= pass[j];
            j++;
            if (j == pass.length) j = 0;
        }

        return intToByte(int_pass);
    }

    //passing an encrypted key and password as a string
    //getting the decrypted key
    public static byte[] decryptKey(byte[] _key, String _pass){
        //user pass to byte
        byte[] pass = _pass.getBytes(StandardCharsets.UTF_8);

        int[] keyInt = byteToInt(_key);
        int j = 0;
        byte[] decrypted_key = new byte[32];
        for (int i = 0; i < decrypted_key.length; i++) {
            int process = keyInt[i] / pass[j];
            process -= pass[j];
            process /= pass[j];
            process -= pass[j];
            decrypted_key[i] = (byte) (process / pass[j]);
            j++;
            if (j == pass.length) j = 0;
        }

        return decrypted_key;
    }

    //the overloaded doFinal method either encrypts or decrypts depending on the mode
    public byte[] doFinal(String _data) {

        byte[] dataBytes = _data.getBytes(StandardCharsets.UTF_8);
        byte[] encDataBytes = null;

        try {
            encDataBytes = cipher.doFinal(dataBytes);
        } catch (BadPaddingException e) { e.printStackTrace();
        } catch (IllegalBlockSizeException e) { e.printStackTrace(); }

        return encDataBytes;
    }
    public String doFinal(byte[] _data) {

        byte[] encDataBytes = null;
        String encData = null;

        try {
            encDataBytes = cipher.doFinal(_data);
        } catch (BadPaddingException e) { e.printStackTrace();
        } catch (IllegalBlockSizeException e) { e.printStackTrace(); }

        if(encDataBytes != null) encData = new String(encDataBytes, StandardCharsets.UTF_8);
        return encData;
    }


    public static byte[] getHashFromPass(String _pass){

        byte[] hash = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            messageDigest.update(_pass.getBytes());

            hash = messageDigest.digest();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }


    public static final byte[] intToByte(int[] _intMass){

        byte[] byteMass = new byte[_intMass.length * 4];

        for (int i= 0; i < _intMass.length; i++) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(_intMass[i]).array();
            byteMass[i * 4] = bytes[0];
            byteMass[(i * 4) + 1] = bytes[1];
            byteMass[(i * 4) + 2] = bytes[2];
            byteMass[(i * 4) + 3] = bytes[3];
        }
        return byteMass;
    }

    public static final int[] byteToInt(byte[] _byteMass){
        int[] massInt = new int[_byteMass.length / 4];

        for (int i = 0; i < _byteMass.length; i += 4) {
            int value = ((_byteMass[i] & 0xFF) << 24) +
                    ((_byteMass[i + 1] & 0xFF) << 16) +
                    ((_byteMass[i + 2] & 0xFF) << 8) +
                    (_byteMass[i + 3] & 0xFF);
            massInt[i / 4] = value;
        }
        return massInt;
    }

}