package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetLoanAccountDetailRequest extends BaseBankRequest{
    private String accountNo;
    private String accountNoOld;        //acc alias
}
