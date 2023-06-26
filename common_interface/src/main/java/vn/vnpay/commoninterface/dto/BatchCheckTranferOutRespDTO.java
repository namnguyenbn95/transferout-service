package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchCheckTranferOutRespDTO {
    private String id;
    private String refNo;
    private String bankCode;
    private String branch;
    private String channel;
    private String currency;
    private String rcvCode;
    private String rcvBnkn;
    private String benCode;
    private String benBnkn;
    private String benDpt;
    private String benDpt1;
    private String fwBrn;
    private String sendCode;
    private String sendName;
    private String channelOrg;
    private String bankCodeName;
}
