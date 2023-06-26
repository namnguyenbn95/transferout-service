package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.RequestContentAcc247Payment;
import vn.vnpay.commoninterface.dto.CreditAccountDTO;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;

@Getter
@Setter
public class ReqContentResp {
    private DebitAccountDTO debitInfo;
    private CreditAccountDTO creditInfo;
    private RequestContentAcc247Payment tranferInfo;
}
