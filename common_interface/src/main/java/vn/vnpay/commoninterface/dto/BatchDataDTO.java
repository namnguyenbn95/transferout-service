package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BatchDataDTO {
    private Long batchId;
    private Long numberItem;
    private String no;
    private String refNo;
    private String fromAcc;
    private String typeTrans;
    private String toAcc;
    private String beneName;
    private String idNo;
    private String issuedDate;
    private String issuedPlace;
    private String beneBank;
    private BigDecimal amount;
    private String ccy;
    private String feeType;
    private String content;
    private String contentErr;
    private String contentErrEN;
    private String serviceCode;
    private String bankCode;
    private String bankName;
    private String beneCityCode;
    private String beneCityName;
    private String beneBranchCode;
    private String beneBranchName;
    private String beneBankCode;
    private String beneBankName;
    private String status;
    private String productCode;
    private String ccyFromAcc;
    private String accNoFromAcc;
    private String accAliasFromAcc;
    private String accTypeFromAcc;
    private String productCodeFromAcc;
}
