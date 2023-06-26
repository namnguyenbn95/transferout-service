package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.AccountDTO;

import java.util.List;

@Getter
@Setter
public class AccountListingBankResponse extends BaseBankResponse {
    private List<AccountDTO> listAccount;
}
