package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetLoanAccountDetailDTO;

@Getter
@Setter
public class GetLoanAccountDetailResponse extends BaseBankResponse{
    private String msgID;
    private String msgDetail;
    private GetLoanAccountDetailDTO accountDetail;
}
