package vn.vnpay.commoninterface.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class FeeTransferDTO {
    BigDecimal fee;
    BigDecimal vat;
    BigDecimal method;
    String ccy;
}
