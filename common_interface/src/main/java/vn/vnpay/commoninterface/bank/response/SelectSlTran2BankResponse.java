package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SLtran2DTO;

import java.util.List;

@Getter
@Setter
public class SelectSlTran2BankResponse extends BaseBankResponse {
    private List<SLtran2DTO> sltran2nDataList;
}
