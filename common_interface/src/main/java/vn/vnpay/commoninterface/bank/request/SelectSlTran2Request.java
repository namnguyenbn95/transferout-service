package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectSlTran2Request extends BaseBankRequest {
    private String refNo;
    private String date6;// 250420
    private String tellId;// 5087
}
