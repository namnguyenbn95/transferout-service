package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutPostingCoreReq {
    private String fromAccNo;
    private String fromAccType;
    private String toAccNo;
    private String toAccType;
    private String amount;
    private String feeAmount;
    private String chargeType;
    private String currCode;
    private String msgId;
    private String sequence;
    private String remark;
    private String hostDate;
    private String tellerID;
    private String pcTime;
    private String vatAmt;
    private String reqKey;
}
