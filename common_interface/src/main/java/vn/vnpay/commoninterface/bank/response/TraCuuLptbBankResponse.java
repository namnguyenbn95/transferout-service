package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RegistrationTaxDTO;

@Getter
@Setter
public class TraCuuLptbBankResponse extends BaseBankResponse {
    private RegistrationTaxDTO registrationTax;
}
