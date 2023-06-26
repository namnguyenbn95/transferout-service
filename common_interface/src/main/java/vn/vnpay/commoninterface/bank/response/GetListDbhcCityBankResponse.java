package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DbhcCityDTO;

import java.util.List;

@Getter
@Setter
public class GetListDbhcCityBankResponse extends BaseBankResponse {
    private List<DbhcCityDTO> listDbhcCity;
}
