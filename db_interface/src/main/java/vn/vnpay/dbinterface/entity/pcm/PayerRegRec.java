package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayerRegRec {
    private String cifNo;
    private SvcIdent svcIdent;
    private String cusRefCode;
    private String fullName;
    private String billPmtStatusCode;
    private String payerBank;
    private PayerAcctId payerAcctID;
    private PayerInfo payerInfo;
    private String pmtType;
    private String prepayDays;
    private String status;
}
