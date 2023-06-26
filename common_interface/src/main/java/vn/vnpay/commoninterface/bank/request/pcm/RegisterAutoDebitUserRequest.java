package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitBillingInfo;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;

@Getter
@Setter
@NoArgsConstructor
public class RegisterAutoDebitUserRequest extends BaseBankRequest {
    DebitAccountDTO accountData;
    AutoDebitBillingInfo billingInfo;
    long sequence;
    String tellerId;
}
