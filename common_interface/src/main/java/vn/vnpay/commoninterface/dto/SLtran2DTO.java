package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SLtran2DTO {
    //250420
    private int date6;
    //2020116
    private int date7;
    //700
    private int srcBrn;
    //5087
    private String tellId;
    //1
    private long stt;
    //0
    private long seq;
    //26052
    private long seq1;
    //202004175087035019
    private String refNo;
    //120101003
    private Long acctNo;
    //G
    private String acType;
    //6800
    private int accBrn;
    //TRUNG GIAN TTDTLNH NHNTVN
    private String accNam;
    //VND
    private String curr;
    // 154668200
    private BigDecimal amt;
    //154693710
    private BigDecimal orgAmt;
    //25520
    private BigDecimal feeAmt;
    //VND
    private String feeCur;
    //IBFPM.202004175087035019.1.INV#796,798,801,803,810,811,820,835,840,843,849,852,856
    private String remark;
    // I
    private String type;
    //EXC
    private String chrgCod;
    //6800
    private int fwBrn;
    //79604001
    private String rcvCode;
    //NH TNHH MTV STANDARD CHARTERED VN CN TP HCM
    private String rcvBnkn;
    //79604001
    private String benCode;
    //NH TNHH MTV STANDARD CHARTERED VN CN TP HCM
    private String benBnkn;
    //99271364099
    private String benAcc;
    //CHAMPION LEE GROUP VIETNAM LTD.
    private String benNam;

    private String info;

    private String benAddr;
    //Benificiary address IBPS 2.5 - SnCode

    private String benCoun;
    //Benificiary country IBPS 2.5 - SndIdNo

    private String debtTyp;
    //Account type of debit account IBPS 2.5 - SndIsSd

    private String debtBrn;
    //Branch of debit account IBPS 2.5 - SndIsSe

    private String debtCur;
    //Currency of Debit account IBPS 2.5 - RvCode

    private String debtAmt;
    //Debit amount including fee amount and VAT amount IBPS 2.5 - BenIdNo

    private String sndAcct;
    //Ordering customer account IBPS 2.5 - BenIsSd

    private String sndName;
    //Ordering customer name IBPS 2.5 - BenIsSe

    private String status;  //Status 1 - Toàn bộ bảng kê hợp lệ 6 - Chấp nhận sai, xử lý sau

    private String errSts;

    private String rsvt1;

    private String rsvt2;

    private String corTellId;

    private int corSeq;

    private String corSupId;

    private int corDate6;

    private int stltbSq;
    //PS sequence number

    private String stltnBr;
    //PS Teller ID

    private int stltTpc;
    //PC time

    private int stltTil;
    //PS Till number

    private String stltPst;
    //PS Status P – Posted R – Rejected

    private String benDpt;

}
