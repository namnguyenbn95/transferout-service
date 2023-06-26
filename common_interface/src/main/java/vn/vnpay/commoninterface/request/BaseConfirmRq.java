package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BaseConfirmRq extends BaseClientRequest {
    private String challenge = "";

    @NotBlank(message = "authenType must not be blank")
    private String authenType;

    @NotBlank(message = "authenValue must not be blank")
    private String authenValue;

    private String tranToken;
    private String tranxId;
}
