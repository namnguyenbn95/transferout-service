package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BHXHDataDTO;

@Getter
@Setter
@Builder
public class BHXHPaymentBankRequest extends BaseBankRequest {
    private int cif;
    private BHXHDataDTO tax;
}
