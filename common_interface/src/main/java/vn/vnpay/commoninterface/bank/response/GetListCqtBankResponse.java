package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CqtDTO;

import java.util.List;

@Getter
@Setter
public class GetListCqtBankResponse extends BaseBankResponse {
    private List<CqtDTO> listCqt;
}
