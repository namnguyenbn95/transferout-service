package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BHXHCityDTO;

import java.util.List;

@Getter
@Setter
public class BHXHGetCityBankResponse extends BaseBankResponse {
    private List<BHXHCityDTO> listCity;
}
