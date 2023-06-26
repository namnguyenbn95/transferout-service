package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardWaitingBankRequest extends BaseBankRequest {
    private String acctNbr;
    private String recordDate;
}
