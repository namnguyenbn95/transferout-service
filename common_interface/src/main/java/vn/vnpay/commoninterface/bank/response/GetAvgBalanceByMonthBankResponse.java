package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAvgBalanceByMonthBankResponse extends BaseBankResponse {
    String avgBalance;
    String currency;
    String msgID;
    String msgDetail;
}
