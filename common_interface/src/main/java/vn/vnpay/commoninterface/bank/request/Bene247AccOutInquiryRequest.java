package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Bene247AccOutInquiryRequest extends BaseBankRequest {
    private RequestContentAcc247Inquiry requestContent;
}
