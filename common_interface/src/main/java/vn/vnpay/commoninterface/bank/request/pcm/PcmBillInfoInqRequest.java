package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.BillSel;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;

@Getter
@Setter
@Builder
public class PcmBillInfoInqRequest extends BaseBankRequest {
    MsgHdr msgHdr;
    String cusRefCode;
    String cifNo;
    BillSel billSel;
}
