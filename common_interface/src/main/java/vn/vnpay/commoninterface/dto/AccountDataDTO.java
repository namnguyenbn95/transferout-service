package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountDataDTO {
    private Integer cif;
    private String accountNumber;
    private String currency;
    private String accountHolderName;
    private Integer branch;
    private Double amount;
    private Double amountVND;
    private String accountNumberOld;
    private Integer branchNew;
    private Double originAmount;
    private String unstructuredData;
    private String accountType;
    private String bankCode;
    private String bankName;
    private Double rate;
}
