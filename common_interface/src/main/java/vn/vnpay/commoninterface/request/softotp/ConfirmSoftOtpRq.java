package vn.vnpay.commoninterface.request.softotp;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseSoftRequest;

@Getter
@Setter
public class ConfirmSoftOtpRq extends BaseSoftRequest {
    private String data;
}
