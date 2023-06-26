package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SvcType {
    String svcCategoryID;
    String svcTypeID;
    String isWithBills;
    String isAdhocPmt;
    String isPayerCharge;
}
