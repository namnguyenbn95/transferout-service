package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TransDetailCharebackDTO;
import vn.vnpay.commoninterface.dto.TransDetailCreateCharebackDTO;
import vn.vnpay.commoninterface.response.BaseClientResponse;

@Getter
@Setter
@Builder
public class GetDetailCreateTransChargebackResp  {
    private TransDetailCharebackDTO transSme;
    private TransDetailCreateCharebackDTO transChargeback;
    private String status;
}
