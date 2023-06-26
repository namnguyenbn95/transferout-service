package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmtFeeInfo {
    // fee
    String feeType;
    String feeIncomeGL;

    BillAmt curAmt;
}
