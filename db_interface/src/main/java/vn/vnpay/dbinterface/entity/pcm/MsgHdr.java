package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.common.CommonUtils;

@Getter
@Setter
public class MsgHdr {
    String svcVersion;
    String rqUID;
    String clientDt;
    String clientTime;
    String clientTerminalSeqNum;
    String channelId;
    String userId;
    String bankEntityId;
    String branchNo;
    String clientTotalSeqNum;

    public MsgHdr(String RqUID, String channelId) {
        this.svcVersion = "1.0";
        this.rqUID = RqUID;
        this.clientDt = CommonUtils.TimeUtils.getNow("yyyy-MM-dd");
        this.clientTime = CommonUtils.TimeUtils.getNow("HH:mm:ss");
        this.clientTerminalSeqNum = CommonUtils.TimeUtils.getNow("yyyyMMddHHmmssSSS");
        this.channelId = channelId;
        this.userId = "Business";
        this.bankEntityId = "IGTBVN";
        this.branchNo = "06800";
        this.clientTotalSeqNum = String.valueOf(CommonUtils.getPcmIndex());
    }
}
