package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardTransDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class CardTransHistListBankResponse extends BaseBankResponse {
    List<CardTransDTO> transactionList;
}
