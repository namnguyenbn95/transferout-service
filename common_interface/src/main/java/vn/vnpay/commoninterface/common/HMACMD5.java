package vn.vnpay.commoninterface.common;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HMACMD5 {
    private static final String ALGORITHM = "HmacMD5";

    public static byte[] createMac(byte[] bData, byte[] keybyte) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKey key = new SecretKeySpec(keybyte, ALGORITHM);
        mac.init(key);
        mac.update(bData);
        return mac.doFinal();
    }

    public static boolean isValidSignature(String requestSign, String genSign, String signKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String serverSign = Base64.getEncoder().encodeToString(HMACMD5.createMac(genSign.getBytes(), signKey.getBytes()));
        return serverSign.equals(requestSign);
    }
}
