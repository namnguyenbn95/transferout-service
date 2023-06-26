package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ListInRateDTO {
    private String rateName;
    private BigDecimal vndRate;
    private BigDecimal eurRate;
    private BigDecimal usdRate;
}
