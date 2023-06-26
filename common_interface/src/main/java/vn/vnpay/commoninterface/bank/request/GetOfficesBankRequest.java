package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetOfficesBankRequest extends BaseBankRequest {
    private String language;
    private long districtID;

}
