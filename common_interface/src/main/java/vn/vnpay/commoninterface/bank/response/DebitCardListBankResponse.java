package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.dto.DebitCardDTO;

import java.util.List;

@Getter
@Setter
public class DebitCardListBankResponse extends BaseBankResponse {
    private List<DebitCardDTO> listCard;
}
