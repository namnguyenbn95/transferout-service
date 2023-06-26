package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BatchCheckTranferOutRespDTO;

import java.util.List;

@Getter
@Setter
public class BatchCheckTranferOutBankResponse extends BaseBankResponse {
    private List<BatchCheckTranferOutRespDTO> benBankInfoList;
}
