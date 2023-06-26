package vn.vnpay.commoninterface.bank.entity.billing;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;
import vn.vnpay.dbinterface.entity.pcm.PayerRegRec;

@Getter
@Setter
public class AutoDebitCustomerInfo {
    String id;
    DebitAccountDTO accountData;
    AutoDebitBillingInfo billingInfo;
    String createdDate;
    String lastChangedDate;
    String companyCode;

    // pcm
    private PayerRegRec payerRegRec;
}
