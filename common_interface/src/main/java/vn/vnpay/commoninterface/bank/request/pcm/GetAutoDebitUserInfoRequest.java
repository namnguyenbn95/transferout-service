package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;

@Getter
@Setter
public class GetAutoDebitUserInfoRequest extends BaseBankRequest {
    String providerCode;
    String vcbServiceCode;
    String customerCode;
}
