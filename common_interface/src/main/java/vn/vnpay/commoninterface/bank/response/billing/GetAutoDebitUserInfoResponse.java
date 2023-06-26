package vn.vnpay.commoninterface.bank.response.billing;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitCustomerInfo;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;

@Getter
@Setter
public class GetAutoDebitUserInfoResponse extends BaseBankResponse {
    AutoDebitCustomerInfo customerInfo;
}
