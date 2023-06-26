package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPaymentDataAndTransLogBankResponse extends BaseBankResponse {
    private String sequence;
    private String hostDate;
}
