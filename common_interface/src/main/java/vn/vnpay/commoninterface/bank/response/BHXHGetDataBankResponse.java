package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BHXHDataDTO;

@Getter
@Setter
public class BHXHGetDataBankResponse extends BaseBankResponse {
    private BHXHDataDTO bhxhData;
}
