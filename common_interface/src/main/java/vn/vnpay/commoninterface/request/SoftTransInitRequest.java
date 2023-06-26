package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftTransInitRequest extends BaseSoftRequest {
    private Long transId;
    private String serviceCode;
    private String providerCode;
    private String fromAccount;
    private String amount;
    private String toAccount;
    private String transToken;
}
