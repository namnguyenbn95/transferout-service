package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletCheckActiveBankResponse extends BaseBankResponse {
    String customerName;
}
