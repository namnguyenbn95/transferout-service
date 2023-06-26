package vn.vnpay.dbinterface.entity.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PmtAmount {
    BillAmt curAmt;
    BigDecimal lceAmt;
    BigDecimal exchangeRate;
}
