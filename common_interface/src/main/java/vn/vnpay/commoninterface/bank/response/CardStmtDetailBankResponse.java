package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardStmtDetailDTO;

@Getter
@Setter
public class CardStmtDetailBankResponse extends BaseBankResponse {
    private CardStmtDetailDTO cardDetail;
}
