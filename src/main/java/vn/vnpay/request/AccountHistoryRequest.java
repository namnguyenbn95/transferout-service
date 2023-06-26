package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AccountHistoryRequest extends BaseClientRequest {

    @NotBlank(message = "accountNo must not be blank")
    private String accountNo;

    @NotBlank(message = "accountType must not be blank")
    private String accountType;

    @NotBlank(message = "isAlias must not be blank")
    private String isAlias;

    @NotBlank(message = "fromDate must not be blank")
    private String fromDate;

    @NotBlank(message = "toDate must not be blank")
    private String toDate;
}
