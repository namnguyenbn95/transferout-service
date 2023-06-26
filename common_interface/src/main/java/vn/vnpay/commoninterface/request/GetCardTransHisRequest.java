package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class GetCardTransHisRequest extends BaseClientRequest {
    @NotBlank(message = "fromDate must not be blank")
    private String fromDate;

    @NotBlank(message = "toDate must not be blank")
    private String toDate;

    @NotBlank(message = "acctNbr must not be blank")
    private String acctNbr;
}

