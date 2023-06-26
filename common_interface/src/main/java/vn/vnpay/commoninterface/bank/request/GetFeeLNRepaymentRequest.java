package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetFeeLNRepaymentRequest extends BaseBankRequest{
    private String lnAccount;
    private String lnAccountAlias;
    private double amountRepayment;
    private String lnAccountCurr;
    private String effectiveDate;
}
