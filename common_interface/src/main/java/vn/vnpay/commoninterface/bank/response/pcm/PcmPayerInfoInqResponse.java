package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.*;

import java.util.ArrayList;

@Getter
@Setter
public class PcmPayerInfoInqResponse extends BaseBankResponse {
    SvcIdent svcIdent;
    MsgHdr msgHdr;
    PayerInfo payerInfo;
    String cusRefCode;
    ArrayList<BillField> billField;
    Status status;

}
