package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.NdktDTO;

import java.util.List;

@Getter
@Setter
public class GetListNdktBankResponse extends BaseBankResponse {
    private List<NdktDTO> listNdkt;
}
