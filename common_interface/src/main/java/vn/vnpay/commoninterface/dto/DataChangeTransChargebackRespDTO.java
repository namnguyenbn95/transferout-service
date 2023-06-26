package vn.vnpay.commoninterface.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataChangeTransChargebackRespDTO {
    @SerializedName("CREDITACCOUNT")
    private String creditAccount;
    @SerializedName("CREDITNAME")
    private String creditName;
    @SerializedName("IDNO")
    private String idNo;
    @SerializedName("ISSUE_DATE")
    private String issueDate;
    @SerializedName("ISSUE_PLACE")
    private String issuePlace;
    @SerializedName("BILLING_CUS_CODE")
    private String billingCusCode;
    @SerializedName("BILLING_CUS_NAME")
    private String billingCusName;
    @SerializedName("AMOUNT")
    private Double amount;
    @SerializedName("REMARK")
    private String remark;
}
