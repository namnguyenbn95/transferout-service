package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletGetLstPartnerBankRequest extends BaseBankRequest {
    private String partnerCategory;
}
