package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeeTransDetailChargebackDTO {
    private String accountno;
    private String accountcurrency;
    private String glaccount;
    private double feeamountogrin;
    private double feeamountvnd;
    private double feeamT_FLAT_VND;
    private double feeamT_VAT_OGRIN;
    private double feeamT_VAT_VND;
    private String teller;
    private int sequece;
    private String hostdate;
    private String pctime;
    private String remark;
}
