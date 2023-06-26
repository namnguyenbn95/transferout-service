package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftTransConfirmRequest extends BaseSoftRequest {
    private String data;
    private String transId;
    private String otpId;
}
