package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TaxPaymentInfoDTO;

@Getter
@Setter
public class GetSoThueBankResponse extends BaseBankResponse {
    private TaxPaymentInfoDTO taxPaymentInfo;
}
