package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitBillingInfo;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;

@Getter
@Setter
public class UnRegAutoDebitUserRequest extends BaseBankRequest {
    AutoDebitBillingInfo billingInfo;
}
