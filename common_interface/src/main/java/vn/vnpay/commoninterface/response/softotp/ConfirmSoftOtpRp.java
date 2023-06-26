package vn.vnpay.commoninterface.response.softotp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmSoftOtpRp {
    private String code;
    private String message = "";
    private String data;
}
