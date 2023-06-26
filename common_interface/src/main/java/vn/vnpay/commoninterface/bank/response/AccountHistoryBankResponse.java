package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.AccountHistoryDTO;

import java.util.List;

@Getter
@Setter
public class AccountHistoryBankResponse extends BaseBankResponse {
    List<AccountHistoryDTO> listHistory;
}
