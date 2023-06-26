package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Bene247AccOutPaymentRequest extends BaseBankRequest{
    private String addInfo;
    private RequestContentAcc247Payment requestContent;
}
