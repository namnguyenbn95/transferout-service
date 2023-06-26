package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TransferOutRequest extends BaseClientRequest {
    @NotBlank(message = "fromAcc must not be blank")
    private String fromAcc;

    @NotBlank(message = "toAcc must not be blank")
    private String toAcc;

    @NotBlank(message = "amount must not be blank")
    private String amount;

    @NotBlank(message = "feeType must not be blank")
    private String feeType; // 1: người chuyển trả; 2: người nhận trả

    @NotBlank(message = "curCode must not be blank")
    private String curCode;

    @NotBlank(message = "transType must not be blank")
    private String transType; // 1: Ngày hiện tại; 2: Tương lai; 3: Định kỳ

    private String content;
    private String futureDate;
    private String fromDate;
    private String toDate;
    private int interval;
    private String intervalUnit; // 1: Ngày; 2: Tuần; 3: Tháng

    private String toAccName;
    private String beneBankCode; // Mã ngân hàng thụ hưởng
    private String beneBankName; // Tên ngân hàng thụ hưởng
    private String accAlias;

    private String beneCityCode;
    private String beneCityName;
    private String beneBranchCode;
    private String beneBranchName;

    // by pass check số dư
    private String isByPassNotBalance;
}
