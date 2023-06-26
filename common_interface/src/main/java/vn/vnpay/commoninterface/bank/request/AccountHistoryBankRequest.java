package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHistoryBankRequest extends BaseBankRequest {
    private String accountNo;
    private String accountType;
    private boolean isAlias;
    private String fromDate;
    private String toDate;
}
