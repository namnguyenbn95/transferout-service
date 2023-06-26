package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountStatusInquiryBankRequest extends BaseBankRequest {
    private String accountNo;
    private String accountType;
    private boolean isAlias;
}
