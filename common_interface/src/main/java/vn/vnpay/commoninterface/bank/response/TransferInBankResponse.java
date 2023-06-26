package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferInBankResponse extends BaseBankResponse {
    private int sequence;
    private String hostDate;
    private String msgID;
}
