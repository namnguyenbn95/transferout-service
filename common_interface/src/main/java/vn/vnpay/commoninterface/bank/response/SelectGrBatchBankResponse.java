package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GrBatchDTO;

@Getter
@Setter
public class SelectGrBatchBankResponse extends BaseBankResponse {
    private GrBatchDTO data;
}
