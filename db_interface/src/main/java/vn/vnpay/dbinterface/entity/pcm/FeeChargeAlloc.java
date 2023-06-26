package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FeeChargeAlloc {
    String chargeRegulation;
    ArrayList<PmtFeeInfo> fee;
    ArrayList<PmtTaxInfo> taxInfo;
}
