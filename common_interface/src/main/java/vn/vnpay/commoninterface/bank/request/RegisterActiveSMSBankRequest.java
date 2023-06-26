package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterActiveSMSBankRequest extends BaseBankRequest {
    private String cif;
    private String phonenumber;
    private String account;
}
