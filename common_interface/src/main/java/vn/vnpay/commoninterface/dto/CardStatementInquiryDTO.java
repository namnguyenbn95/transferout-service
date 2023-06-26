package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardStatementInquiryDTO {
    private String totalPaidAmount;
    private String minAmount;
    private String stmtBalance;
    private String outstdBalance;
    private String proposedPaymentDate;
    private String lastStmtDate;
    private String cif;
    private String cardHolderName;
    private String cardType;
    private String cutOffTime;
}
