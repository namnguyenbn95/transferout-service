package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutInquiryResponse{
    private Bene247AccOutRespStatus responseStatus;
    private ResponseContentAcc247Inquiry responseContent;
}
