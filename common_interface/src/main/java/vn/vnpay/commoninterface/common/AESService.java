package vn.vnpay.commoninterface.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author daond
 */
public class AESService {

    private static Logger logger = LoggerFactory.getLogger(AESService.class);

    public static final int AES_KEYLENGTH = 256;
    public static final int IV_LENGTH = 16;
    private static final String AES_KEY = "sHaRaJ0Pk9KWfBx1LNZQ03115u2w5ALLrrYYaMvnh1w=";

    /**
     * Gen initialization vector
     *
     * @return
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom prng = new SecureRandom();
        prng.nextBytes(iv);
        return iv;
    }

    /**
     * Gen key
     *
     * @return
     * @throws Exception
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEYLENGTH);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Mã hóa chuỗi text
     *
     * @param text
     * @return
     * @throws Exception
     */
    public static String encrypt(String text) throws Exception {
        // Gen init vector
        byte[] iv = generateIV();
        logger.debug("iv: {}", new String(Base64.getEncoder().encode(iv)));

        // Decode base64 aes key
        byte[] decodedKey = Base64.getDecoder().decode(AES_KEY);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        // Cấu hình các tham số cho AES
        Cipher aesCipherForEncryption = Cipher.getInstance("AES/CTR/NoPadding");
        aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] byteDataToEncrypt = text.getBytes("UTF-8");

        // Mã hóa
        byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
        logger.debug("data: {}", new String(Base64.getEncoder().encode(byteCipherText)));

        // Phần byte đầu IV, các byte tiếp theo là data
        byte[] finalEncrypt = new byte[iv.length + byteCipherText.length];
        System.arraycopy(iv, 0, finalEncrypt, 0, iv.length);
        System.arraycopy(byteCipherText, 0, finalEncrypt, iv.length, byteCipherText.length);
        String strEncryptData = new String(Base64.getEncoder().encode(finalEncrypt));
        return strEncryptData;
    }

    /**
     * Giải mã
     *
     * @param decrypt
     * @return
     * @throws Exception
     */
    public static String decrypt(String decrypt) throws Exception {
        // Decode base64 aes key
        byte[] decodedKey = Base64.getDecoder().decode(AES_KEY);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        // Tách lấy phần byte của iv và data
        byte[] allEncryptByte = Base64.getDecoder().decode(decrypt.getBytes());
        byte[] iv = Arrays.copyOfRange(allEncryptByte, 0, IV_LENGTH);
        byte[] textByte = Arrays.copyOfRange(allEncryptByte, IV_LENGTH, allEncryptByte.length);

        // Cấu hình các tham số cho AES
        Cipher aesCipherForDecryption = Cipher.getInstance("AES/CTR/NoPadding");
        aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Giải mã
        byte[] byteCipherText = aesCipherForDecryption.doFinal(textByte);
        String strCipherText = new String(byteCipherText, "UTF-8");
        return strCipherText;
    }

    public static void main(String[] args) throws Exception {
        String strSecretKey = AESService.generateKey();
        String data = "Vnpay@demo123!@#";
        logger.info("Secret key: " + strSecretKey);
        logger.info("Original data: " + data);

        try {
            /*===== Mã hóa =====*/
            String encryptedData = encrypt(data);
            logger.info("encryptedData: " + encryptedData);

            /*===== Giải mã =====*/
            String decryptedData = decrypt(encryptedData);
            logger.info("decryptedData: " + decryptedData);
            if (decryptedData.equals(data)) {
                logger.info("true");
            }
        } catch (Exception ex) {
            logger.info("ex: ", ex);
        }
    }
}
