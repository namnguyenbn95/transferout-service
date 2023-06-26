package vn.vnpay.commoninterface.ott;

import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author nam
 */
public class HMAC256 {

    private static final String ALGORITHM = "HmacSHA256";

    public static byte[] createMac(byte[] bData, byte[] keybyte)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKey key = new SecretKeySpec(keybyte, ALGORITHM);
        mac.init(key);
        mac.update(bData);
        return mac.doFinal();
    }

    public static String generateAccessToken(String phone, String key) {
        try {
            AccessTokenEntity ate = new AccessTokenEntity();
            ate.setToken(UUID.randomUUID().toString());
            ate.setTimestamp(Long.toString(System.currentTimeMillis()));
            String sign = phone + ate.getToken() + ate.getTimestamp();
            sign = new String(Base64.encode(HMAC256.createMac(sign.getBytes(), key.getBytes())));
            ate.setSign(sign);
            Gson gson = new Gson();
            String accessToken = gson.toJson(ate);
            return new String(Base64.encode(accessToken.getBytes()));
        } catch (Exception ex) {
        }
        return "";
    }
}
