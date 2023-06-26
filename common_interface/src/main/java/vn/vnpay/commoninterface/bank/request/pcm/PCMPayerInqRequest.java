package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.BillFieldPayerInq;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;
import vn.vnpay.dbinterface.entity.pcm.SvcIdent;

import java.util.ArrayList;

@Getter
@Setter
public class PCMPayerInqRequest extends BaseBankRequest {
    MsgHdr msgHdr;
    SvcIdent svcIdent;
    String cusRefCode;
    ArrayList<BillFieldPayerInq> billField;
}
