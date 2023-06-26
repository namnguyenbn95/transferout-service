package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;

@Getter
@Setter
public class PcmListBillerRequest extends BaseBankRequest {
    MsgHdr msgHdr;
    String svcCategoryID;
    String svcTypeID;
    String billerID;

    public PcmListBillerRequest(
            String svcCategoryID, String svcTypeID, String requestId, String channelId) {
        this.getRequestHeader().setTraceNo(requestId);
        this.msgHdr = new MsgHdr(requestId, channelId);
        this.svcCategoryID = svcCategoryID;
        this.svcTypeID = svcTypeID;
        this.billerID = "";
    }
}
