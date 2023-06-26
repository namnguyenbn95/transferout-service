package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.HardTokenTransactionDTO;

@Getter
@Setter
public class HardTokenAuthenRequest extends BaseBankRequest {
    private int cif;
    private String username;
    private String userBranch;
    private int tokenType;
    private String challenge;
    private String otp;
    private HardTokenTransactionDTO transaction;
}
