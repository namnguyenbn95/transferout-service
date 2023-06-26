package vn.vnpay.commoninterface.bank.request.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;

@Getter
@Setter
public class BLGWQueryRequest extends BaseBankRequest {
    String vcbCode;
    String customerCode;
    String filler;
    String tellerId;
    String billType;
}
