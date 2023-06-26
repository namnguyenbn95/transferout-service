package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransDetailChargebackDTO {
    private String futurE_DATE;
    private String futurE_TRAN_DATE;
    private String autodebiT_CHANNEL_REG;
    private String beN_BANK_CODE;
    private String beN_BANK_NAME;
    private String provideR_CODE;
    private String provideR_NAME;
    private String billinG_CUS_CODE;
    private String billinG_CUS_NAME;
    private String ecoM_REF;
    private String qR_MERCHANT_ID;
    private String qR_BILL_ID;
    private int cif;
    private String customercode;
    private String debitaccount;
    private String debitname;
    private double debitbranch;
    private String creditaccount;
    private String creditname;
    private String creditbranch;
    private double amount;
    private double debitamount;
    private double creditamount;
    private double feeamount;
    private double vat;
    private String currency;
    private String debitcurrency;
    private String creditcurrency;
    private String feetype;
    private String remark;
    private String teller;
    private double sequence;
    private String status;
    private String datE_TIME;
    private String hostdate;
    private String trantype;
    private String errorcode;
    private String errordetail;
    private String idno;
    private String issuE_DATE;
    private String issuE_PLACE;
    private String channel;
    private String transactioN_NAME;
    private String trantypE_MAP;
    private String rM_FW_BRANCH;
    private String tsoL_BRANCH_ACCEPT;
    private String tranS_CARD_NUM;
    private String tranS_CARD_KEY;
    private String tsoL_CUS_COMMENT;
    private double traN_AMOUNTVND;
    private String traN_MERCHANT;
    private String traN_AUTHNO;
    private String traN_DEVICEID;
    private String traN_TRACENO;
    private String debiT_CARD_STATUS;
    private int iS_AUTO_FEE;
    private RewardCardDTO rewarD_CARD;
    private RewardRedeemDTO rewarD_REDEEM;
    private RewardAccountDTO rewarD_ACCOUNT;
}
