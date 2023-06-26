package vn.vnpay.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.AccountHistoryDTO;

import java.util.List;

@Getter
@Setter
public class AccountHistoryResponse {
    List<AccountHistoryDTO> listHistory;
}
