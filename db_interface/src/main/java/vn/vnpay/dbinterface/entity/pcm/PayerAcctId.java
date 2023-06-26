package vn.vnpay.dbinterface.entity.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PayerAcctId {
    AcctId acctID;
    BankInfo bankInfo;
}
