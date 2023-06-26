package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DebitAccountDTO {
    private int cif;
    private String accountNo;
    private String accountAlias;
    private String currency;
    private String accountHolderName;
    private String branch;
    private double amountVND;
    private double originAmount;
    private String accountType;
    private String rate;
    private String bankCode;
    private String bankName;
    private String accountAddress;
    private String accountName;
    private String panNumber;
    private boolean panToken;
}
