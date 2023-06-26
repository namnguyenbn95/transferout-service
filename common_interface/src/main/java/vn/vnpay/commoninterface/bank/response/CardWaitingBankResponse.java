package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardWaitingDTO;

import java.util.List;

@Getter
@Setter
public class CardWaitingBankResponse extends BaseBankResponse {
    private List<CardWaitingDTO> waitingList;
}
