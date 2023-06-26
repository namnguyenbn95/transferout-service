package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CustomsTaxInfoDTO;

@Getter
@Setter
public class TraCuuPhiHqBankResponse extends BaseBankResponse {
    private CustomsTaxInfoDTO customsTaxInfo;
}
