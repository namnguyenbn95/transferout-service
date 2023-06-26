package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;

import java.math.BigDecimal;

@Getter
@Setter
public class BLGWQueryResponse extends BaseBankResponse {
    BigDecimal amount;
    String serverTime;
    String customerName;
    String customerCode;
    String sourceAccountName;
    String destinationAccountName;
    String pinBlock;
    String track2;
    String vcbCode;
    String addInfo;
}
