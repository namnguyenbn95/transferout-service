package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpDto {
    private String otpId;
    private String otpMessage;
    private String otpExpire;
}
