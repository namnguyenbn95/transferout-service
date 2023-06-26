package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CalculateExrateBankRequest extends BaseBankRequest {
    private String currencyCode;
    private double amount;

}
