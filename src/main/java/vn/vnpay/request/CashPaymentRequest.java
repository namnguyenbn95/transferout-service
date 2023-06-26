package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CashPaymentRequest extends BaseClientRequest {
    @NotBlank(message = "fromAcc must not be blank")
    private String fromAcc;

    @NotBlank(message = "amount must not be blank")
    private String amount;

    @NotBlank(message = "curCode must not be blank")
    private String curCode;

    @NotBlank(message = "feeType must not be blank")
    private String feeType;     // 1: người chuyển trả; 2: người nhận trả

    @NotBlank(message = "fullname must not be blank")
    private String fullname;

    @NotBlank(message = "idType must not be blank")
    private String idType;

    @NotBlank(message = "id must not be blank")
    private String id;

    @NotBlank(message = "issuedDate must not be blank")
    private String issuedDate;

    @NotBlank(message = "issuedPlace must not be blank")
    private String issuedPlace;

    private String idIssuedPlace;

    private String accAlias;

    private String content;

    private String isByPassNotBalance;
}
