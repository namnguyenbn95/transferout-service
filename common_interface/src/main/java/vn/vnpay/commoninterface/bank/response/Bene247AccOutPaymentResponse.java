package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutPaymentResponse {
    private Bene247AccOutAdviceResp adviceResponse;
    private Bene247AccOutRespStatus responseStatus;
    private Bene247AccOutPostedResp postedResponse;
    private Bene247AccOutReqContentResp requestContent;
}

