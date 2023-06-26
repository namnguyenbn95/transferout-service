package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqHeaderResp {
    private String traceNo;
    private String mobileNo;
    private String clientIP;
    private String appServerIP;
    private String channel;
    private String osName;
    private String osVersion;
    private String deviceID;
    private String clientID;
    private String deviceImei;
    private String requestID;
}
