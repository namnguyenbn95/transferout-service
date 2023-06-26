package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardStmtHistDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class CardStmtHistListBankResponse extends BaseBankResponse {
    List<CardStmtHistDTO> statementHistList;
}
