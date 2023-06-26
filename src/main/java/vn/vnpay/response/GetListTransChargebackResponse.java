package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListTransChargebackRespClientDTO;
import vn.vnpay.commoninterface.response.BaseClientResponse;

import java.util.List;

@Getter
@Setter
@Builder
public class GetListTransChargebackResponse {
    List<ListTransChargebackRespClientDTO> listTransChargeback;
}
