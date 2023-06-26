package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetListFeeCodeComboBankReq extends BaseBankRequest {
    private String feeCode;
    private String status;
}
