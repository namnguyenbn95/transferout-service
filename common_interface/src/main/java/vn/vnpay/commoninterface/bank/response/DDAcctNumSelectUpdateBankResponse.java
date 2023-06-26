package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DDAcctNumSelectUpdateBankResponse extends BaseBankResponse {
    private String acctNo;
    private String acctType;
    private String glCostCenter;
    private String glSerial;
    private String msgID;
    private String msgDetail;
}

