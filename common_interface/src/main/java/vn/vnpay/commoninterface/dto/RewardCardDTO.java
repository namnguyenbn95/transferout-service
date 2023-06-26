package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardCardDTO {
    private String rW_TRANSDATE;
    private String rW_POSTINGDATE;
    private String rW_CARDTYPE;
    private String rW_TOKENCARD;
    private double rW_ORGAMOUNT;
    private String rW_ORGCURRENCY;
    private double rW_TXNAMOUNT;
    private String rW_TXNCURRENCY;
    private String rW_MERCHANT;
    private String rW_AUTHCODE;
    private String rW_ACCOUNT;
    private String rW_ACCOUNTREF;
    private String rW_CARD_TXNID;
    private String rW_CARD_MASK;
    private String rW_TRAN_CODE;
    private String rW_LOCATION_TXN;
    private String rW_CARD_MCC_ID;
    private String rW_AGENCY_ID;
}
