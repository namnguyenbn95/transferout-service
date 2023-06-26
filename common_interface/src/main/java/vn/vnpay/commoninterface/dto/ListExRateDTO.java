package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListExRateDTO {
    private String currencyCode;
    private String currencyName;
    private double buy;
    private double transfer;
    private double sell;

}
