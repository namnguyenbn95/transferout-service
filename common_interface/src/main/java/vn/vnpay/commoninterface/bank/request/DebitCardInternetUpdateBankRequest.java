package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DebitCardInternetUpdateBankRequest extends BaseBankRequest {
    private String panHash;
    private String panMash;
    private String username;
    private String status;
}
