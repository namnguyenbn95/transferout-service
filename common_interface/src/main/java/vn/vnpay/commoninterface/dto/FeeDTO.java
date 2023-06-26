package vn.vnpay.commoninterface.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDTO {
    private double amount;
    private double vatAmount;
    private double amountVND;
    private double vatAmountVND;
    private String currency;
    private int type;
    private String chargeType;
    private int authMethod;
    private double originAmount;
    private double originVatAmount;
    private double originAuthMethod;
}
