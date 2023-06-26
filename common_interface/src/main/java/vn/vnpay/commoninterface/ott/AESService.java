/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.commoninterface.ott;

/**
 * @author quangtt
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * https://www.owasp.org/index.php/Using_the_Java_Cryptographic_Extensions
 */
public class AESService {


    //private byte[] iv = Base64.decode("3dhvgqxXT2R2/NuAsRdMNw==");
    public static final int AES_KEYLENGTH = 128;
    public static final int IV_LENGTH = 16;
//    private String secretKey;

    public static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = 0;
        }

//        SecureRandom prng = new SecureRandom();
//        prng.nextBytes(iv);
        return iv;
    }

    public static byte[] encrypt(String strSecretKey, String text, byte[] iv) throws Exception {
        byte[] encodedKey = strSecretKey.getBytes();
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        Cipher aesCipherForEncryption = Cipher.getInstance("AES/CTR/NoPadding");
        aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] byteDataToEncrypt = text.getBytes("UTF-8");
        byte[] byteCipherText = aesCipherForEncryption
                .doFinal(byteDataToEncrypt);
        return byteCipherText;

    }

    public static byte[] encryptByte(String strSecretKey, byte[] byteDataToEncrypt, byte[] iv) throws Exception {
        byte[] encodedKey = strSecretKey.getBytes();
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        Cipher aesCipherForEncryption = Cipher.getInstance("AES/CTR/NoPadding");
        aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] byteCipherText = aesCipherForEncryption
                .doFinal(byteDataToEncrypt);
        return byteCipherText;

    }


    public static String decrypt(String strSecretKey, byte[] textByte, byte[] iv) throws Exception {
        byte[] encodedKey = strSecretKey.getBytes();
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        Cipher aesCipherForDecryption = Cipher.getInstance("AES/CTR/NoPadding");

        aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] byteCipherText = aesCipherForDecryption
                .doFinal(textByte);
        String strCipherText = new String(byteCipherText, "UTF-8");
        return strCipherText;


    }

}
