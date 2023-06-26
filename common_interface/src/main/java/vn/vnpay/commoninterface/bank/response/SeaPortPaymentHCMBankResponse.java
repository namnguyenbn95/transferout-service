package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeaPortPaymentHCMBankResponse extends BaseBankResponse {
    private String sequence;
    private String hostDate;
    private String msgID;
}
