package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.*;

import java.util.ArrayList;

@Getter
@Setter
public class PCMCustomerBillingRequest extends BaseBankRequest {
    MsgHdr msgHdr;
    String cifNo;
    SvcIdent svcIdent;
    String registrationType;
    String cusRefCode;
    String fullName;
    String billPmtStatusCode;
    String payerBank;
    PayerAcctId payerAcctID;
    PayerInfo payerInfo;
    ArrayList<BillField> billField;
    String pmtType;
    String prepayDays;
}
