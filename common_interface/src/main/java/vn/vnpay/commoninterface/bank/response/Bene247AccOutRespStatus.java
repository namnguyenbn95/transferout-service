package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutRespStatus {
    private String responseCode;
    private String responseMessage;
    private String traceNo;
}
