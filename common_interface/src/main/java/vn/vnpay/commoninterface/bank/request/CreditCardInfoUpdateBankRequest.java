package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreditCardInfoUpdateBankRequest extends BaseBankRequest {
    private double rowNum;
    private String xpacCode;
}
