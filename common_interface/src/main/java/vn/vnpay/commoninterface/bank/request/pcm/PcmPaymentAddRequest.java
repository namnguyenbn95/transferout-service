package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.dbinterface.entity.pcm.AddlFieldMetaData;
import vn.vnpay.dbinterface.entity.pcm.MsgHdr;
import vn.vnpay.dbinterface.entity.pcm.PmtInfo;

import java.util.ArrayList;

@Getter
@Setter
public class PcmPaymentAddRequest extends BaseBankRequest {
    String cusRefCode;
    String cifNo;
    String payerInternalID;
    PmtInfo pmtInfo;
    ArrayList<AddlFieldMetaData> addlFieldMetaData;

    public PcmPaymentAddRequest(String channelId) {
        this.msgHdr = new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId);
    }

    MsgHdr msgHdr;
}
