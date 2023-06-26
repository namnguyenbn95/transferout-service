package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCustomerAcctBankResponse extends BaseBankResponse {
    private String acctNo;
    private String cifNo;
    private String msgID;
    private String msgDetail;
}
