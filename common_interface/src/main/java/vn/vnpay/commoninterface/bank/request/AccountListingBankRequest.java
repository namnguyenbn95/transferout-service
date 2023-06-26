package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountListingBankRequest extends BaseBankRequest {
    private String cif;
    private String accountGroupType;
    private boolean joinable;
}
