package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetSeaPortPaymentInfoHCMDTO;

@Getter
@Setter
@Builder
public class SeaPortPaymentHCMBankRequest extends BaseBankRequest {
    private int cif;
    private GetSeaPortPaymentInfoHCMDTO tax;
}
