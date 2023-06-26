package vn.vnpay.commoninterface.ott;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author nam
 */
public class HMACMD5 {
    private static final String ALGORITHM = "HmacMD5";

    public static byte[] createMac(byte[] bData, byte[] keybyte) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKey key = new SecretKeySpec(keybyte, ALGORITHM);
        mac.init(key);
        mac.update(bData);
        return mac.doFinal();
    }

//    public static void hmac256()
//    {
//        try
//        {
//            String text ="123";
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//
//            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
//            System.out.println("hash: " + new String(Hex.encode(hash)));
//            byte[] keybyte ="key".getBytes();
//            byte[] bData ="hello".getBytes();
//            Mac mac = Mac.getInstance("HmacSHA256");
//            SecretKey key = new SecretKeySpec(keybyte, ALGORITHM);
//            mac.init(key);
//            mac.update(bData);
//            System.out.println("mac: " + new String(Hex.encode(mac.doFinal())));
//
//
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//    }

//    public static String createMac(String data, String keystr) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
//        Mac mac = Mac.getInstance(ALGORITHM);
//        SecretKey key = new SecretKeySpec(data.getBytes(), ALGORITHM);
//        mac.init(key);
//        mac.update(data.getBytes("UTF-8"));
//        return new String(Base64.encode(mac.doFinal()));
//    }


}
