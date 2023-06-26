package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.HardTokenTransactionDTO;

@Getter
@Setter
public class GetTokenChallengeRequest extends BaseBankRequest {
    private int cif;
    private String username;
    private String userBranch;
    private int tokenType;
    private HardTokenTransactionDTO transaction;
}
