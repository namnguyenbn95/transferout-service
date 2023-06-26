package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SLtran1DTO;

@Getter
@Setter
public class SelectSlTran1BankResponse extends BaseBankResponse {
    private SLtran1DTO data;
}
