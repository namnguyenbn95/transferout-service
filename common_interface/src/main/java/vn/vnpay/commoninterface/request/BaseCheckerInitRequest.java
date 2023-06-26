package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BaseCheckerInitRequest extends BaseClientRequest {
    @NotBlank(message = "tranxId must not be blank")
    private String tranxId;
    private String reason;
}
