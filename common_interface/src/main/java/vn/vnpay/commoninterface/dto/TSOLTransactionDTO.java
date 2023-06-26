package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TSOLTransactionDTO {
    private String servicE_NAME;
    private String servicE_NAME_EN;
    private String reasoN_NAME;
    private String reasoN_NAME_EN;
    private String requesT_NAME;
    private String requesT_NAME_EN;
    private int id;
    private String tsoL_REF;
    private int cuS_CIF;
    private String cuS_ACCOUNT;
    private String cuS_FULLNAME;
    private int traN_SEQUENCE;
    private String traN_TELLER;
    private String traN_HOSTDATE;
    private int tsoL_SERVICE_CODE;
    private String tsoL_STATUS;
    private String tsoL_TELLER_ACCEPT;
    private String creatE_DATE;
    private String traN_CHANNEL;
    private String tsoL_DEPARTMENT;
    private String tsoL_COMMENT;
    private String oriG_TRAN_DETAIL;
    private String tsoL_TRAN_DETAIL;
    private int requesT_CODE;
    private int reasoN_CODE;
    private String cuS_CODE;
    private String telleR_ACCEPT_DATE;
    private String telleR_FINISH_DATE;
    private double traN_AMOUNT;
    private String traN_CURRENCY;
    private String traN_REMARK;
    private String traN_PCTIME;
    private String tsoL_BRANCH_ACCEPT;
    private String tranS_CARD_NUM;
    private String tranS_CARD_KEY;
    private double traN_AMOUNTVND;
    private String traN_MERCHANT;
    private String traN_AUTHNO;
    private String traN_DEVICEID;
    private String traN_TRACENO;
    private int feE_STATUS;
    private String feE_TRAN_DETAIL;
    private double feeamT_FLAT_VND;
    private double feeamT_VAT_VND;
    private double typE_REQUEST;
    private String searcH_KEY;
    private String rboL_STATUS;
    private String rboL_ACTION;
    private int rboL_FEEDCODE;
    private String rboL_TELLER_APR;
    private String rboL_APR_COMMENT;
    private String cuS_EMAIL;
    private String emaiL_STATUS;
    private String telleR_REQUEST_DATE;
    private String suP_APPROVE_DATE;
}
