package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeeDataDTO {
    private Double amount;
    private Double vatAmount;
    private String currency;
    private Integer type;
    private Integer authMethod;
    private Double originAmount;
    private Double originVatAmount;
    private Double originAuthMethod;
}
