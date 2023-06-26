package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.*;

import java.util.ArrayList;

@Getter
@Setter
public class PcmBillInfoInqResponse extends BaseBankResponse {
    SvcIdent svcIdent;
    String internalCusRefCode;
    String cusRefCode;
    String cifNo;
    String isPayerCharge;
    String pmtSeq;
    ArrayList<BillRec> billRec;
    ArrayList<AddlFieldMetaData> addlFieldMetaData;
    PmtRec pmtRec;

    PayerInfo payerInfo;
    ArrayList<BillField> billField;
    String directCreditFlg;
    String billerAcctNo;
}
