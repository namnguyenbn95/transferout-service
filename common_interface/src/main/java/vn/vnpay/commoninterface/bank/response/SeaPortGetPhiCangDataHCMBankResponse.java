package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetSeaPortPaymentInfoHCMDTO;

@Getter
@Setter
public class SeaPortGetPhiCangDataHCMBankResponse extends BaseBankResponse {
    private GetSeaPortPaymentInfoHCMDTO tax;
}
