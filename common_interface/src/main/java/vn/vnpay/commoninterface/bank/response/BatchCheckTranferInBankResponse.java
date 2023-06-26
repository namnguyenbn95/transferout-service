package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BatchCheckTranferInDTO;

import java.util.List;

@Getter
@Setter
public class BatchCheckTranferInBankResponse extends BaseBankResponse {
    private List<BatchCheckTranferInDTO> ddMastInfoList;
}
