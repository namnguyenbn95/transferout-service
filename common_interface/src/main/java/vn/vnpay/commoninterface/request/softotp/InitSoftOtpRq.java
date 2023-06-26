package vn.vnpay.commoninterface.request.softotp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.commoninterface.request.BaseSoftRequest;

@Getter
@Setter
@Slf4j
public class InitSoftOtpRq extends BaseSoftRequest {
    private String data = "";
}
