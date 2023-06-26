package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRegisterBankRequest extends BaseBankRequest {
    private String operation;
    private int cif;
    private String username;
    private String userBranch; // Mã chi nhánh của user thực hiện
    private String customerName;
    private int tokenType; // Loại token (7: Hard Token)
    private String serialNo; // Serial của token
}
