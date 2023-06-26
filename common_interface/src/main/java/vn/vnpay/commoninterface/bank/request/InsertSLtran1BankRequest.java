package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SLtran1DTO;

@Getter
@Setter
@Builder
public class InsertSLtran1BankRequest extends BaseBankRequest {
    private SLtran1DTO sltran1nData;
}
