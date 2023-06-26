package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BatchCheckTranferOutReqDTO;

import java.util.List;

@Getter
@Setter
public class BatchCheckTranferOutBankRequest extends BaseBankRequest {
    private List<BatchCheckTranferOutReqDTO> benBankInputList;
}
