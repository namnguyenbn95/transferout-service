package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;
import vn.vnpay.dbinterface.entity.pcm.SvcIdent;

@Getter
@Setter
public class PcmPaymentConfigRequest extends BaseBankRequest {
    MsgHdr msgHdr;
    SvcIdent svcIdent;
    String pmtType;

    public PcmPaymentConfigRequest(
            String catId,
            String svcId,
            String billerId,
            String pmtType,
            String requestId,
            String channelId) {
        this.getRequestHeader().setTraceNo(requestId);
        this.msgHdr = new MsgHdr(requestId, channelId);
        this.svcIdent = SvcIdent.builder().svcCategory(catId).svcType(svcId).billerId(billerId).build();
        this.pmtType = pmtType;
    }
}
