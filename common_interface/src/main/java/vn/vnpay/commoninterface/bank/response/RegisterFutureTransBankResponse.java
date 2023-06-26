package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterFutureTransBankResponse extends BaseBankResponse {
    private String transId;
    private String teller;
    private String hostDate;
}
