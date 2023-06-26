package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CardStatementUpdateBankRequest extends BaseBankRequest {
    private String debitAccount;
    private String debitCurrency;
    private int debitBranch;
    private String debitName;
    private int cif;
    private String cardAccount;
    private String tellerID;
    private int sequence;
    private String traceNo;
    private int tellerBranch;
    private String chargeType;
    private String channelRemark;
    private String vipClass;
    private String toGLCostCenter;
    private String pcTime;
    private double debitAmountVND;
    private double debitAmount;
    private double creditAmount;
    private double creditAmountVND;
    private String creditCurrency;
    private double transferFee;
    private double transferFeeVND;
    private double feeVat;
    private double feeVatVND;
    private String feeCurrency;
}
