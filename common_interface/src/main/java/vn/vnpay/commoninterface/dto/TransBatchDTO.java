package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class TransBatchDTO {
    private long id;
    private String tranxType;
    private String createdUser;
    private String createdMobile;
    private String checkerAuthenType;
    private String approvedUser;
    private LocalDateTime approvedDate;
    private String approvedMobile;
    private String fromAcc;
    private String toAcc;
    private String providerCode;
    private String serviceCode;
    private double amount;
    private String tranxNote;
    private String status;
    private LocalDateTime tranxTime;
    private String resBankCode;
    private String resBankDesc;
    private double flatFee;
    private double feeOnAmt;
    private double totalAmount;
    private String ccy;
    private String tranxRemark;
    private String productType;
    private String tranxRefno;
    private String beneBranchCode;
    private String branchCode;
    private String serviceType;
    private String feeType;
    private String creditName;
    private String beneBankCode;
    private String cusName;
    private String cifNo;
    private String makerAuthenType;
    private String tranxContent;
    private String metadata;
    private String channel;
    private String reason;
    private List<String> listTransId;

    private String authenType;
    private String challenge;
    private String lang;
    private String source;

    private double totalVnd;
    private double totalUsd;
}