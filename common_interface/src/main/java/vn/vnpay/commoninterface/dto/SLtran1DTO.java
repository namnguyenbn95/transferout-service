package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SLtran1DTO {
    //250420 hostDate
    private int date6;
    //2020116
    private int date7;
    //700
    private int srcBrn;
    //5087
    private String tellId;
    //202004175087035019
    private String refNo;
    //71001258678
    private Long accFrm;
    //nullable - "D"
    private String accTyp;
    //"CT TNHH TAG-IT PACIFIC VIETNAM"
    private String accNam;
    //700
    private Integer accBrn;
    // VND - nullable
    private String accCur;
    //154693710
    private BigDecimal amt;
    //IBFPM.202004175087035019.
    private String remark1;
    //25520
    private BigDecimal feeAmt;
    //VND
    private String feeCur;
    //430101008
    private long feeAcc;
    // N
    private String deferPmt;
    //1
    private String status;
    private int errSts;
    private int errFee;
    // Reserve text 1 Single Debit (S)/Multi Debit (M)
    private String rsvt1;
    private long seq;
    private String rsvt2;
    private int stltBsq;
    private String stltPst;
    private int stltTpc;
    private String stltRej;
}
