package vn.vnpay.dbinterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class DebitCardDTO {
    // Số CIF trên DB thẻ
    String cardNumber;
    String primaryAccount;
    String secondaryAccount;
    String panHash;
    String expiryDate;
    int status;
    String blockCode;
    String issueDate;
    String emBossName;
    String branch;
    String subBranch;
    String panShort;
    String cardType;
    String productID;
    String cif;
    String vcbToken;

    // to transfer
    String issueBranch;
}
