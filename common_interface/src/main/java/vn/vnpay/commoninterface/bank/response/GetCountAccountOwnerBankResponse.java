package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCountAccountOwnerBankResponse extends BaseBankResponse {
    private int countOwner;
    private String msgID;
    private String msgDetail;
}
