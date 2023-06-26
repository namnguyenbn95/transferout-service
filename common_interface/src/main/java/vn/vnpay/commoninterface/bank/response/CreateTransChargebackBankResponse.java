package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransChargebackBankResponse extends BaseBankResponse {
    private String tsoL_REF;
    private String feeStatus;
    private String feeComment;
    private String hostDate;
    private String msgDetail;
}
