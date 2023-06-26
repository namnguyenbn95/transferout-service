package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetListTransChargebackDTO;

import java.util.List;

@Getter
@Setter
public class GetListTransChargebackBankResponse extends BaseBankResponse{
    private List<GetListTransChargebackDTO> listTSOL_TRANSACTION;
    private String msgDetail;
}
