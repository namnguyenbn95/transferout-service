package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.EcomMidDTO;

import java.util.List;

@Getter
@Setter
public class GetEcomMidListBankResponse extends BaseBankResponse {
    private List<EcomMidDTO> ecomMidList;
}
