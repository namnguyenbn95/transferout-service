package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LimitEcomDTO {
    private String accNo;
    private Double minAmount;
    private Double maxAmount;
    private Double maxAmountDay;
    private String tid;
    private String mid;
}
