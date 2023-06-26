package vn.vnpay.dbinterface.entity.pcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BillAmt {
    BigDecimal amt;
    String curCode;
}
