package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.BillAmt;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;
import vn.vnpay.dbinterface.entity.pcm.SvcIdent;

@Getter
@Setter
public class PcmPaymentFeeRequest extends BaseBankRequest {

    public PcmPaymentFeeRequest(String channelId) {
        this.msgHdr = new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId);
    }

    MsgHdr msgHdr;
    SvcIdent svcIdent;
    BillAmt billAmt;
}
