package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ProvincesDTO;

import java.util.List;

@Getter
@Setter
public class GetProvincesBankResponse extends BaseBankResponse {
    private List<ProvincesDTO> data;
}
