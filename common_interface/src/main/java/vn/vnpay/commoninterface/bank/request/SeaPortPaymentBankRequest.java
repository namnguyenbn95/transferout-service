package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetSeaPortPaymentInfoDTO;

@Getter
@Setter
@Builder
public class SeaPortPaymentBankRequest extends BaseBankRequest {
    private int cif;
    private GetSeaPortPaymentInfoDTO tax;
}
