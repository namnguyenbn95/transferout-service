package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BLGWPayRequest extends BaseBankRequest {
    String creditAccountNo;
    String debitAccountNo;
    String debitAccountName;
    String vcbCode;
    BigDecimal amount;
    String customerCode;
    String tellerId;
    long sequence;
    long branch;
    String pcTime;
    String hostDate;
}
