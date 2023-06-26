package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.KbctDTO;

import java.util.List;

@Getter
@Setter
public class GetListKbctBankResponse extends BaseBankResponse {
    private List<KbctDTO> listKbct;
}
