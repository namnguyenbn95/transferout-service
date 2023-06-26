package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.MbService;

import java.util.List;

@Getter
@Setter
@Builder
public class GetListServiceCodeChargebackResp {
    private List<MbService> serviceCodes;
}
