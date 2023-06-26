package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.OfficesDTO;

import java.util.List;

@Getter
@Setter
public class GetOfficesBankResponse extends BaseBankResponse {
    private List<OfficesDTO> data;
}
