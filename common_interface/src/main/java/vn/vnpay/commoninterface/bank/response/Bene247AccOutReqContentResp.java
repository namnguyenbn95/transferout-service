package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutReqContentResp {
    private String addInfo;
    private ReqHeaderResp requestHeader;
    private ReqContentResp requestContent;
}
