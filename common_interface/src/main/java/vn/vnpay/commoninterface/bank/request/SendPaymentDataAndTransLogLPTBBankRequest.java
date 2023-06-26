package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RegistrationTaxDTO;

@Getter
@Setter
@Builder
public class SendPaymentDataAndTransLogLPTBBankRequest extends BaseBankRequest {
    private RegistrationTaxDTO registrationTax;
}
