package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CardStmtHistDTO {
    private String txnAmt;
    private String orgAmt;
    private String trnDate;
    private String trnCode;
    private String postingDate;
    private String orgCurr;
    private String txnDesc;
    private String acqRef;
}
