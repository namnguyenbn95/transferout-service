package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLoanAccountDetailDTO {
    private String accountStatus;
    private String accountName;
    private String cif;
    private String creditCode;
    private String contractNumber;
    private double currentBalance;
    private String openDate;
    private String lastDrawDate;
    private String profitAdd;
    private String maturityDate;
    private double interest;
    private int duration;                       // kì hạn
    private String durationCode;                // mã kì hạn
    private String customerAddress;
    private String loanType;
    private String accountType;
    private String accuedPenaltyCharge;
    private double paymentAmount;
    private String nextPaymentDueDate;
    private String nextInterestDueDate;
    private String currencyType;
    private String originalLoanDate;
    private String branchNumber;
    private double prinDueAmount;               // gốc đến hạn
    private String curCode;
}
