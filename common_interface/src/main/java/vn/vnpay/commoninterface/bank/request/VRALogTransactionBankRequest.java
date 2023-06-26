package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VRALogTransactionBankRequest extends BaseBankRequest {
    private int cif;
    private String ddAccount;
    private String ddAccountName;
    private String vrAccount;
    private String vrAccountName;
    private double amount;
    private String currency;
    private String tellerID;
    private int sequence;
    private int branch;
    // 2021-07-29T16:09:27.167Z
    private String hostDate;

    // 2021-07-29T16:09:27.167Z
    private String vdate;
    private String status;
    private String ref;
    private String chrgCod;
    private double chrgAmt;
    private String chrgCurr;
    private double rate;
    private String dbAcct;
    private String dbName;
    private String benBank;
    private String rmInfo;

    // 2021-07-29T16:09:27.167Z
    private String createTime;
}
