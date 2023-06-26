package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DDAcctNumSelectUpdateBankRequest extends BaseBankRequest {
    private String acctNo;
    private String branchNo;
    private String userID;
    private String channel;
    private String price;
}
