package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcomMidDTO {
    private String securitiesName;
    private String securitiesID;
    private double maxAmount;
    private double minAmount;
    private double maxAmountDay;
}
