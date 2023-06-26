package vn.vnpay.commoninterface.response.softotp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitSoftOtpRp {
    private String code;
    private String message;
    private String otpId;
    private String otpMessage;
    private String otpTimeExpire;
}
