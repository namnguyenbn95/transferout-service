package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetIBPSTransStatusBankResponse extends BaseBankResponse {
    private int status;
    private String serialNo;
    private String msgDetail;
}
