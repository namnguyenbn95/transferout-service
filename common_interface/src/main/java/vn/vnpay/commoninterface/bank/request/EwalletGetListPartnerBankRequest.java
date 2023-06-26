package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletGetListPartnerBankRequest extends BaseBankRequest {
    String partnerCategory;
}
