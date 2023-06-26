package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

@Getter
@Setter
public class GetListTransChargebackReq extends BaseClientRequest {
    private String fromDate;
    private String toDate;
    private String tsolRef;
    private String maker;
    private String checker;
}
