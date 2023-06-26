package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.request.BaseBankRequest;

@Getter
@Setter
public class GetListTransChargebackRequest extends BaseBankRequest {
    private String fromDate;
    private String toDate;
}
