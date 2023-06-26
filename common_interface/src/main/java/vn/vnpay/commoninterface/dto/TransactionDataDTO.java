package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionDataDTO {
    private AccountDataDTO debitAccount;
    private AccountDataDTO creditAccount;
    private RecipientIDDTO recipient;
    private FeeDataDTO fee;
    private Double amount;
    private Double originAmount;
    private String txnType;
    private String usecase;
    private String txnId;
    private String currency;
    private String originCurrency;
    private String content;
    private String remark;
    private String tellerId;
    private CronJobDTO cronJob;
    private Integer tellerBranch;
    private Integer sequence;
    private Integer orgSequence;
    private Integer traceId;
    private String pcTime;
    private String advice;
}
