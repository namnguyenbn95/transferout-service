package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetSeaPortPaymentInfoDTO;

@Getter
@Setter
public class SeaPortGetPhiCangDataBankResponse extends BaseBankResponse {
    private GetSeaPortPaymentInfoDTO tax;
}
