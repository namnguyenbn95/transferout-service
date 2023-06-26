package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardStatementUpdateBankResponse extends BaseBankResponse {
    private String transID;
    private String hostDate;
}
