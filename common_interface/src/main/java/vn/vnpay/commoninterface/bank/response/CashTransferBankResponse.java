package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CashTransferBankResponse extends BaseBankResponse{
    private int sequence;
    private String rmmastRefNo;
    private String hostDate;
    private String msgID;
}
