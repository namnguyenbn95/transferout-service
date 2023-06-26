package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.dto.CardDTO;

import java.util.List;

@Getter
@Setter
public class CardListBankResponse extends BaseBankResponse {
    private List<CardDTO> listCard;
}
