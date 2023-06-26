package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.FutureTransDataDTO;

@Getter
@Setter
@Builder
public class RegisterFutureTransBankRequest extends BaseBankRequest {
    private FutureTransDataDTO transData;
}
