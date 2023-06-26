package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TaxPaymentInfoDTO;

@Getter
@Setter
@Builder
public class SendPaymentDataAndTransLogBankRequest extends BaseBankRequest {
    private int cif;
    private TaxPaymentInfoDTO taxPaymentInfo;
}
