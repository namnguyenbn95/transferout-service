package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SLtran1DTO;
import vn.vnpay.commoninterface.dto.SLtran2DTO;

import java.util.List;

@Getter
@Setter
@Builder
public class InsertSLtranBankRequest extends BaseBankRequest {
    private List<SLtran2DTO> sltran2nDataList;
    private SLtran1DTO sltran1nData;
}
