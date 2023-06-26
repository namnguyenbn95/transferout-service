package vn.vnpay.commoninterface.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeTransDetailChargebackRespDTO {
    @SerializedName("ACCOUNTNO")
    private String accNo;
    @SerializedName("ACCOUNTCURRENCY")
    private String ccy;
    @SerializedName("GLACCOUNT")
    private String glAcc;
    @SerializedName("FEEAMOUNTOGRIN")
    private Double feeAmtOrigin = 0.0;
    @SerializedName("FEEAMOUNTVND")
    private Double feeAmtVND = 0.0;
    @SerializedName("FEEAMT_FLAT_VND")
    private Double feeAmtFlatVND = 0.0;
    @SerializedName("FEEAMT_VAT_OGRIN")
    private Double feeAmtVatOrigin = 0.0;
    @SerializedName("FEEAMT_VAT_VND")
    private Double feeAmtVatVND = 0.0;
    @SerializedName("SEQUECE")
    private Double seq;
    @SerializedName("HOSTDATE")
    private String hostDate;
    @SerializedName("PCTIME")
    private String pcTime;
    @SerializedName("REMARK")
    private String remark;
}
