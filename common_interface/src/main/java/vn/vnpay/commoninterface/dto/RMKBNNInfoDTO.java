package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMKBNNInfoDTO {
    // Kenh giao dich
    private String rmChannel;
    private String rmAccount;
    private String rmAccountName;
    private String rmForwardBranch;
    private String rmSendBankCode;
    private String rmSendBankName;
    private String rmReceiveBankCode;
    private String rmReceiveBankName;
    private String rmBenBankCode;
    private String rmBenBankName;
    private String rmBenDPT;
    private String rmBenName;
    private String rmBenAddress;
    private String rmReserved4;
    private String bankCode;
}
