package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftTransInitResponse {
    private String data;
    private String code;
    private String message;
    private String otpId;
}
