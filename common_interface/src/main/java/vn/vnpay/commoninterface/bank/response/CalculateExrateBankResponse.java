package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateExrateBankResponse extends BaseBankResponse {
    private String currencyName;
    private double buy;
    private double sell;
    private double buyCalc;
    private double sellCalc;
}
