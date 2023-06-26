package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CashFlowDateDTO;

import java.util.List;

@Getter
@Setter
public class GetCashFlowListBankResponse extends BaseBankResponse {
    String msgDetail;
    List<CashFlowDateDTO> listCashFlowOut;
}
