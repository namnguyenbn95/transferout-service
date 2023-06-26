package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.EcomTidDTO;

import java.util.List;

@Getter
@Setter
public class GetEcomTidListBankResponse extends BaseBankResponse {
    private List<EcomTidDTO> ecomTidList;
}
