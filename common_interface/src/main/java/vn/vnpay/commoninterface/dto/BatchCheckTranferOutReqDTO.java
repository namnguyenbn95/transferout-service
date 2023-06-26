package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchCheckTranferOutReqDTO {
    private String id;
    private String refNo;
    private String bankCode;
    private String branch;
    private String channel;
    private String currency;
}
