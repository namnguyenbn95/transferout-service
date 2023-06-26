package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

@Getter
@Setter
public class CheckCutOffTimeRequest extends BaseClientRequest {
    private String serviceCode;
}
