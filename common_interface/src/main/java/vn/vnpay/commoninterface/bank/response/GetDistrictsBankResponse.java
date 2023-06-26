package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DistrictDTO;

import java.util.List;

@Getter
@Setter
public class GetDistrictsBankResponse extends BaseBankResponse {
    private List<DistrictDTO> data;
}
