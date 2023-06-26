package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Getter
@Setter
@Slf4j
public class BaseSoftRequest {
    private String mobileNo;
    private String username;
    private String sessionid;
    private String cifno;
    private String cusname;
    private String lang;
    private String deviceid;
    private String devicemodel;
    private String deviceType;
    private String channel = "6015";
    private String typeCancel;
    private String ip;
    private String requestId = MDC.get("traceId");
    private String signData;
    private long requestTime = System.currentTimeMillis();
    String attachedRoot = "";           // Trạng thái root
    String attachedHook = "";           // Trạng thái hook

    public static String signData(String username, String mobileNo, long requestTime) {
        try {
            String clearData = "sme@0905#@" + username + "##321@vpn" + mobileNo + requestTime;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(clearData.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encode(encodedhash));
        } catch (Exception e) {
            log.error("softotpSignData ex:" + e.getMessage(), e);
            return "";
        }
    }
}
