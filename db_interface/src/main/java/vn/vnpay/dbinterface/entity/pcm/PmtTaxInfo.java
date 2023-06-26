package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmtTaxInfo {
    // tax
    String taxType;
    String taxIncomeGL;

    BillAmt curAmt;
}
