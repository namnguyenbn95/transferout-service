package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

@Getter
@Setter
public class CheckBene247Request extends BaseClientRequest {
    private String toAcc;
    private String beneBankCode;    // Mã ngân hàng thụ hưởng

    private String cardToken;
    private String type;            // ACCOUNT, CARD
}
