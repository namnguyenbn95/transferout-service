package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchCheckTranferInDTO {
    private String accountAlias;
    private String branchNo;
    private String accountNo;
    private String accountType;
    private String curCode;
    private String accountName;
    private int cif;
    private String accountStatus;
    private String productCode;
}
