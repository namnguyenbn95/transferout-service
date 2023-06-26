package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SLtran2DTO;

import java.util.List;

@Getter
@Setter
@Builder
public class InsertSLtran2BankRequest extends BaseBankRequest {
    private List<SLtran2DTO> sltran2nDataList;
}
