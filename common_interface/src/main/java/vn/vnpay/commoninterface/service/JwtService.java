package vn.vnpay.commoninterface.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String secretKey = "1YcBB+.j~rD@2A:ip<v_SeT-RR?KE^HM}yGOznvQ2*5L4Lff08rgzEkWG6<TFbQ";
    private static final String tokenHeader = "{\"alg\": \"HS256\",\"typ\": \"JWT\"}";
    private static final String macInstance = "HmacSHA256";
    private static final int timeExp = 1;
    private final Gson gson;

    public String genJwtToken(String sessionId) throws NoSuchAlgorithmException, InvalidKeyException {
        LocalDateTime ldt = LocalDateTime.now().plusHours(timeExp);
        Long expires = ldt.toEpochSecond(ZoneOffset.UTC);
        JsonObject payload = new JsonObject();
        payload.addProperty("sid", sessionId);
        payload.addProperty("exp", expires);
        payload.addProperty("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        payload.addProperty("jti", UUID.randomUUID().toString());

        return genEncodeHeader() + "." + encode(payload) + "." + genSignature(payload);
    }

    private String genEncodeHeader() {
        return encode(gson.fromJson(tokenHeader, JsonObject.class));
    }

    private String genSignature(JsonObject payload) throws InvalidKeyException, NoSuchAlgorithmException {
        return hmacSha256(genEncodeHeader() + "." + encode(payload));
    }

    public boolean verifyToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        String encodedHeader = genEncodeHeader();
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.error("invalid token");
            return false;
        }
        if (!encodedHeader.equals(parts[0])) {
            log.error("header not matched");
            return false;
        }
        JsonObject payload = gson.fromJson(decode(parts[1]), JsonObject.class);
        if (payload.isJsonNull()) {
            log.error("payload is empty");
            return false;
        }
        if (!payload.has("exp")) {
            log.error("invalid time expire");
            return false;
        }
        String signature = parts[2];

        return payload.get("exp").getAsLong() > (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) //token not expired
                && signature.equals(hmacSha256(encodedHeader + "." + encode(payload))); //signature matched
    }

    private String hmacSha256(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hash = secretKey.getBytes(StandardCharsets.UTF_8);

        Mac sha256Hmac = Mac.getInstance(macInstance);
        SecretKeySpec k = new SecretKeySpec(hash, macInstance);
        sha256Hmac.init(k);

        byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return encode(signedBytes);
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String encode(JsonObject obj) {
        return encode(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    public JsonObject getPayLoad(String token) {
        String[] parts = token.split("\\.");

        return gson.fromJson(decode(parts[1]), JsonObject.class);
    }
}
