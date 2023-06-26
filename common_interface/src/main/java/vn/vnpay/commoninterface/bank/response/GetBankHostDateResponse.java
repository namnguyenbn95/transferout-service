package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetBankHostDateResponse extends BaseBankResponse {

    private String currentDate;

    private String nextDate;

    // MsgID esb
    // example:871e8435-9c39-4a57-8f98-3271d6b01c28
    private String msgID;

    // MsgDetail esb
    private String msgDetail;
}
