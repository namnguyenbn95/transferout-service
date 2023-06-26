package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPaymentDataAndTransLogLPTBBankResponse extends BaseBankResponse {
    private String sequence;
    private String hostDate;
}
