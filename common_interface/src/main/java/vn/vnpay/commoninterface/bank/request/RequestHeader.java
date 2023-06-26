package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;

@Getter
@Setter
public class RequestHeader {

    private String traceNo = MDC.get("traceId");
    private String mobileNo;
    private String clientIP = "unknown";
    private String appServerIP;
    private String channel = "SME";
    private String gui;
    private String os;
    private String osName;
    private String osVersion;
    private String deviceId;
    private String clientId;
    private String emei;
    private String deviceToken;
    private String requestID;
}
