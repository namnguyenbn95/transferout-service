package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HardTokenLockRequest extends BaseBankRequest {
    private int cif;
    private String username;
    private String userBranch;
    private int tokenType;
}
