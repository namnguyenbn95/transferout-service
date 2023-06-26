package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardStmtDetailBankRequest extends BaseBankRequest {
    private String acctNbr;
    private String recordDate;
}
