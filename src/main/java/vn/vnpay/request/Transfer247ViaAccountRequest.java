package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Transfer247ViaAccountRequest extends BaseClientRequest {
    @NotBlank(message = "fromAcc must not be blank")
    private String fromAcc;

    @NotBlank(message = "toAcc must not be blank")
    private String toAcc;

    private String cardMaskingNumber;

    @NotBlank(message = "amount must not be blank")
    private String amount;

    @NotBlank(message = "feeType must not be blank")
    private String feeType; // 1: người chuyển trả; 2: người nhận trả

    @NotBlank(message = "curCode must not be blank")
    private String curCode;

    private String content;

    private String toAccName;
    private String beneBankCode; // Mã ngân hàng thụ hưởng
    private String beneBankName; // Tên ngân hàng thụ hưởng
    private String accAlias;

    private String futureDate;

    private String isByPassNotBalance;
}
