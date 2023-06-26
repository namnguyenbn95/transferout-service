package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.AccountSelectDTO;

import java.util.List;

@Getter
@Setter
public class GetDDAcctNumSelectPriceInqResponse extends BaseBankResponse {
    String acctNo;
    String binValue;
    String binType;
    String binPrice;
    String msgID;
    String msgDetail;
    String binNumberChar;

}
