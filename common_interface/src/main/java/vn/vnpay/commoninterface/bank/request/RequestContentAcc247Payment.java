package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CreditAccountDTO;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;
import vn.vnpay.commoninterface.dto.FeeDTO;
import vn.vnpay.commoninterface.dto.RequestContentAcc247TransInfo;

@Getter
@Setter
@Builder
public class RequestContentAcc247Payment {
    private CreditAccountDTO creditInfo;
    private DebitAccountDTO debitInfo;
    private RequestContentAcc247TransInfo tranferInfo;
}
