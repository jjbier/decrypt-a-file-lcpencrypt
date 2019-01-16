package com.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

public class Main {

    public static byte[] decrypt(byte[] encryptedIvTextBytes, String key) throws Exception {
        int ivSize = 16;
        int keySize = 32;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        SecretKeySpec secretKeySpec = new SecretKeySpec(DatatypeConverter.parseBase64Binary(key), "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/NoPadding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);


        return decrypted;
    }

    static void fileProcessor(String key,File inputFile,File outputFile){
        try {


            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = Main.decrypt(inputBytes, key);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String key = "Y1TkZKlfUHD9eVxkBJ8Edtjnhb3Gk7TL+0uBlnDxd2o=";
        File encryptedFile = new File("/tmp/encripted.epub");
        File decryptedFile = new File("/tmp/decrypted-text.epub");

        try {
           Main.fileProcessor(key,encryptedFile,decryptedFile);
            System.out.println("Sucess");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}

