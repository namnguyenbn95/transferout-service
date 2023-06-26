package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HardTokenInquiryRequest extends BaseBankRequest {
    private int cif;
    private String username;
    private String userBranch;
    private String serialNo;
    private int tokenType;           // 7: Hard token
}
