package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.AccountSelectDTO;

import java.util.List;

@Getter
@Setter
public class GetDDAcctNumSelectListInqResponse extends BaseBankResponse {
    List<AccountSelectDTO> acctList;
    String msgID;
    String msgDetail;
}
