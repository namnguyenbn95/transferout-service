package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckBene247ViaAccountBankRequest extends BaseBankRequest {
    private String benAccount;
    private String benBank;
}
